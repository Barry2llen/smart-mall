package edu.nchu.mall.services.flash_sale.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.nchu.mall.components.feign.coupon.CouponFeignClient;
import edu.nchu.mall.components.feign.product.ProductFeignClient;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.models.model.Try;
import edu.nchu.mall.models.vo.SeckillSessionVO;
import edu.nchu.mall.models.vo.SeckillSkuRelationVO;
import edu.nchu.mall.models.vo.SkuInfoVO;
import edu.nchu.mall.services.flash_sale.service.DelayMessageSender;
import edu.nchu.mall.services.flash_sale.service.FlashSaleService;
import edu.nchu.mall.services.flash_sale.rentity.FlashSaleCleanupMessage;
import edu.nchu.mall.services.flash_sale.rentity.FlashSaleRelatedSkuInfo;
import edu.nchu.mall.services.flash_sale.rentity.FlashSaleSession;
import edu.nchu.mall.services.flash_sale.vo.SessionRelatedSkuInfoVO;
import edu.nchu.mall.services.flash_sale.vo.SessionVO;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.stream.Collectors;

@Slf4j
@Service
@RefreshScope
public class FlashSaleServiceImpl implements FlashSaleService {

    public static final String FLASH_SALE_SESSIONS_KEY = "flash_sale:sessions";
    public static final String FLASH_SALE_SESSIONS_INFO_KEY = "flash_sale:sessions_info"; // 存储sessionId到FlashSaleSession的映射，方便查询
    public static final String FLASH_SALE_SESSION_SKUS_KEY_PREFIX = "flash_sale:session_skus:"; // 后面跟sessionId
    public static final String FLASH_SALE_SKU_SEMAPHORE_KEY_PREFIX = "flash_sale:semaphore:"; // 后面跟随机码，存储每个SKU的库存信号量
    public static final String FLASH_SALE_SESSION_DIGEST_KEY = "flash_sale:session_digest"; // field: sessionId, value: session内容摘要
    public static final String FLASH_SALE_CLEANUP_META_KEY = "flash_sale:cleanup_meta"; // field: sessionId, value: FlashSaleCleanupMessage

    @Value("${flashsale.cache.timeout-seconds:86400}") // 默认24小时
    private long flashSaleCacheTimeoutMs;

    @Autowired
    ProductFeignClient productFeignClient;

    @Autowired
    CouponFeignClient couponFeignClient;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    DelayMessageSender delayMessageSender;

    @Override
    public void uploadFlashSaleSkusToRedis_3d() throws Exception {
        var rTry = Try.of(couponFeignClient::latest3DaysSeckillSessions);
        if (rTry.failed() || rTry.getValue().getCode() != RCT.SUCCESS) {
            log.error("Failed to fetch latest 3 days seckill sessions: {}", rTry.getValue() == null ? rTry.getEx().getMessage() : rTry.getValue().getMsg(), rTry.getEx());
            return;
        }

        // 将数据存入Redis，使用ZSet以结束时间为score，方便后续查询即将结束的活动
        List<SeckillSessionVO> sessions = rTry.getValue().getData();
        for (SeckillSessionVO session : sessions) {
            if (session == null || session.getId() == null || session.getStartTime() == null || session.getEndTime() == null) {
                log.warn("Skip loading flash sale session because required fields are missing: {}", session);
                continue;
            }

            List<SeckillSkuRelationVO> skuRelations = session.getRelations();
            if (skuRelations == null || skuRelations.isEmpty()) {
                log.warn("Skip loading flash sale session {} because sku relations are empty", session.getId());
                continue;
            }

            final String sessionIdStr = session.getId().toString();
            final String sessionSkusKey = FLASH_SALE_SESSION_SKUS_KEY_PREFIX + session.getId();
            final String newDigest = buildSessionDigest(session);
            final Object oldDigestObj = redisTemplate.opsForHash().get(FLASH_SALE_SESSION_DIGEST_KEY, sessionIdStr);
            final String oldDigest = oldDigestObj == null ? null : oldDigestObj.toString();
            final boolean sessionSkusExists = Boolean.TRUE.equals(redisTemplate.hasKey(sessionSkusKey));

            if (newDigest.equals(oldDigest) && sessionSkusExists) {
                log.info("Skip reloading flash sale session {} because content digest is unchanged", session.getId());
                continue;
            }

            if (oldDigest != null && !oldDigest.equals(newDigest) && !LocalDateTime.now().isBefore(session.getStartTime())) {
                log.warn("Skip reloading flash sale session {} because content changed after session start", session.getId());
                continue;
            }

            if (oldDigest != null && !oldDigest.equals(newDigest)) {
                cleanupSemaphoresBySessionSkusHscan(session.getId().toString(), sessionSkusKey);
            }

            List<Long> skuIds = skuRelations.stream().map(SeckillSkuRelationVO::getSkuId).toList();
            var skuInfoTry = Try.of(productFeignClient::getBatch, skuIds);

            if (skuInfoTry.failed() || skuInfoTry.getValue().getCode() != RCT.SUCCESS) {
                log.error("Failed to fetch SKU info for session {}: {}", session.getId(), skuInfoTry.getValue() == null ? skuInfoTry.getEx().getMessage() : skuInfoTry.getValue().getMsg(), skuInfoTry.getEx());
                throw new Exception("Failed to fetch SKU info");
            }

            Map<Long, SkuInfoVO> skuInfoMap = skuInfoTry.getValue().getData();
            if (skuInfoMap.size() != skuIds.size()) {
                log.error("Mismatch in SKU info count for session {}: expected {}, got {}", session.getId(), skuIds.size(), skuInfoMap.size());
                throw new Exception("Mismatch in SKU info count");
            }

            if (!skuIds.stream().allMatch(skuInfoMap::containsKey)) {
                log.error("Some SKU IDs not found in product service for session {}: expected {}, got {}", session.getId(), skuIds, skuInfoMap.keySet());
                throw new Exception("Some SKU IDs not found in product service");
            }

            List<FlashSaleRelatedSkuInfo> skuInfos = skuRelations.stream().map(skuRelation -> {
                SkuInfoVO skuInfo = skuInfoMap.get(skuRelation.getSkuId());

                // 将秒杀相关信息合并到SKU信息中
                FlashSaleRelatedSkuInfo flashSaleRelatedSkuInfo = new FlashSaleRelatedSkuInfo();
                BeanUtils.copyProperties(skuInfo, flashSaleRelatedSkuInfo);
                BeanUtils.copyProperties(skuRelation, flashSaleRelatedSkuInfo);
                flashSaleRelatedSkuInfo.setRandomCode(UUID.randomUUID().toString().replace("-", "")); // 生成一个随机码，后续用于验证请求合法性

                return flashSaleRelatedSkuInfo;
            }).toList();

            FlashSaleSession flashSaleSession = new FlashSaleSession();
            BeanUtils.copyProperties(session, flashSaleSession);

            String sessionJson = mapper.writeValueAsString(flashSaleSession);
            redisTemplate.opsForZSet().add(FLASH_SALE_SESSIONS_KEY, sessionIdStr, flashSaleSession.getEndTime().atZone(java.time.ZoneId.systemDefault()).toEpochSecond());
            redisTemplate.opsForHash().put(FLASH_SALE_SESSIONS_INFO_KEY, sessionIdStr, sessionJson);

            skuInfos.forEach(each -> {
                RSemaphore semaphore = redissonClient.getSemaphore(FLASH_SALE_SKU_SEMAPHORE_KEY_PREFIX + each.getRandomCode());
                semaphore.trySetPermits(each.getSeckillCount());
            });

            redisTemplate.opsForHash().putAll(sessionSkusKey, skuInfos.stream().collect(Collectors.toMap(info -> info.getSkuId().toString(), info -> {
                try {
                    return mapper.writeValueAsString(info);
                } catch (Exception e) {
                    log.error("Failed to serialize SKU info for SKU {}: {}", info.getSkuId(), e.getMessage(), e);
                    return "{ \"error\": \"Failed to serialize SKU info\" }";
                }
            })));

            redisTemplate.opsForHash().put(FLASH_SALE_SESSION_DIGEST_KEY, sessionIdStr, newDigest);

//            LocalDateTime cleanTime = session.getEndTime().plusSeconds(flashSaleCacheTimeoutMs);
            LocalDateTime cleanTime = LocalDateTime.now().plusSeconds(30);
            FlashSaleCleanupMessage cleanupMessage = new FlashSaleCleanupMessage(session.getId(), newDigest, cleanTime);
            redisTemplate.opsForHash().put(FLASH_SALE_CLEANUP_META_KEY, sessionIdStr, mapper.writeValueAsString(cleanupMessage));
            delayMessageSender.sendDelayMessage(cleanupMessage, cleanTime);
        }
    }

    private String buildSessionDigest(SeckillSessionVO session) {
        String relationsText = session.getRelations().stream()
                .sorted(Comparator.comparing(SeckillSkuRelationVO::getSkuId, Comparator.nullsLast(Long::compareTo)))
                .map(rel -> String.join(",",
                        String.valueOf(rel.getSkuId()),
                        String.valueOf(rel.getPromotionId()),
                        String.valueOf(rel.getSeckillPrice()),
                        String.valueOf(rel.getSeckillCount()),
                        String.valueOf(rel.getSeckillLimit()),
                        String.valueOf(rel.getSeckillSort())))
                .collect(Collectors.joining("|"));

        String text = String.join("#",
                String.valueOf(session.getId()),
                String.valueOf(session.getName()),
                String.valueOf(session.getStartTime()),
                String.valueOf(session.getEndTime()),
                String.valueOf(session.getStatus()),
                relationsText);

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] digest = messageDigest.digest(text.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to build flash sale session digest", e);
        }
    }

    private void cleanupSemaphoresBySessionSkusHscan(String sessionId, String sessionSkusKey) {
        ScanOptions scanOptions = ScanOptions.scanOptions().count(200).build();
        try (Cursor<Map.Entry<Object, Object>> cursor = redisTemplate.opsForHash().scan(sessionSkusKey, scanOptions)) {
            while (cursor.hasNext()) {
                Map.Entry<Object, Object> entry = cursor.next();
                try {
                    FlashSaleRelatedSkuInfo skuInfo = mapper.readValue((String) entry.getValue(), FlashSaleRelatedSkuInfo.class);
                    String randomCode = skuInfo.getRandomCode();
                    if (randomCode == null || randomCode.isBlank()) {
                        log.warn("Skip deleting semaphore for session {} because randomCode is empty, skuField={}", sessionId, entry.getKey());
                        continue;
                    }
                    redissonClient.getSemaphore(FLASH_SALE_SKU_SEMAPHORE_KEY_PREFIX + randomCode).delete();
                } catch (Exception e) {
                    log.error("Failed to process SKU info during cache cleanup for session {} and skuField {}: {}", sessionId, entry.getKey(), e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public void cleanFlashSaleSessionCache(String sessionId) {
        // 删除场次信息
        redisTemplate.opsForHash().delete(FLASH_SALE_SESSIONS_INFO_KEY, sessionId);
        // 删除库存信号量
        final String sessionSkusKey = FLASH_SALE_SESSION_SKUS_KEY_PREFIX + sessionId;
        cleanupSemaphoresBySessionSkusHscan(sessionId, sessionSkusKey);
        // 删除商品信息
        redisTemplate.unlink(sessionSkusKey);
        // 从ZSet中删除场次ID
        redisTemplate.opsForZSet().remove(FLASH_SALE_SESSIONS_KEY, sessionId);
        redisTemplate.opsForHash().delete(FLASH_SALE_SESSION_DIGEST_KEY, sessionId);
        redisTemplate.opsForHash().delete(FLASH_SALE_CLEANUP_META_KEY, sessionId);
    }

    @Override
    public List<SessionVO> getSession(Boolean withExpired, Boolean withProducts, Integer pageNum, Integer pageSize) {
        boolean includeExpired = Boolean.TRUE.equals(withExpired);
        boolean includeProducts = Boolean.TRUE.equals(withProducts);
        int safePageNum = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
        long offset = (long) (safePageNum - 1) * safePageSize;
        long nowEpochSecond = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();

        var sessionIdSet = includeExpired
                ? redisTemplate.opsForZSet().range(FLASH_SALE_SESSIONS_KEY, offset, offset + safePageSize - 1L)
                : redisTemplate.opsForZSet().rangeByScore(FLASH_SALE_SESSIONS_KEY, nowEpochSecond, Double.POSITIVE_INFINITY, offset, safePageSize);
        if (sessionIdSet == null || sessionIdSet.isEmpty()) {
            return List.of();
        }

        List<Object> sessionIdFields = sessionIdSet.stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .collect(Collectors.toList());
        if (sessionIdFields.isEmpty()) {
            return List.of();
        }
        List<Object> sessionJsons = redisTemplate.opsForHash().multiGet(FLASH_SALE_SESSIONS_INFO_KEY, sessionIdFields);
        if (sessionJsons == null || sessionJsons.isEmpty()) {
            return List.of();
        }

        List<SessionVO> result = new ArrayList<>();
        for (int i = 0; i < sessionIdFields.size(); i++) {
            Object sessionField = sessionIdFields.get(i);
            Object sessionJsonObj = sessionJsons.get(i);
            if (sessionJsonObj == null) {
                log.warn("Skip flash sale session because session detail is missing in redis hash, sessionId={}", sessionField);
                continue;
            }

            String sessionJson = sessionJsonObj.toString();
            FlashSaleSession flashSaleSession;
            try {
                flashSaleSession = mapper.readValue(sessionJson, FlashSaleSession.class);
            } catch (Exception e) {
                log.warn("Skip flash sale session because session detail parse failed, sessionId={}", sessionField, e);
                continue;
            }

            SessionVO sessionVO = new SessionVO();
            BeanUtils.copyProperties(flashSaleSession, sessionVO);

            if (includeProducts) {
                String sessionSkusKey = FLASH_SALE_SESSION_SKUS_KEY_PREFIX + flashSaleSession.getId();
                List<Object> skuJsons = redisTemplate.opsForHash().values(sessionSkusKey);
                if (skuJsons.isEmpty()) {
                    sessionVO.setSkuInfos(List.of());
                } else {
                    List<SessionRelatedSkuInfoVO> skuInfos = new ArrayList<>();
                    for (Object skuJsonObj : skuJsons) {
                        if (skuJsonObj == null) {
                            continue;
                        }
                        try {
                            FlashSaleRelatedSkuInfo skuInfo = mapper.readValue(skuJsonObj.toString(), FlashSaleRelatedSkuInfo.class);
                            SessionRelatedSkuInfoVO skuInfoVO = new SessionRelatedSkuInfoVO();
                            BeanUtils.copyProperties(skuInfo, skuInfoVO);
                            skuInfos.add(skuInfoVO);
                        } catch (Exception e) {
                            log.warn("Skip flash sale sku because sku detail parse failed, sessionId={}, payload={}", flashSaleSession.getId(), skuJsonObj, e);
                        }
                    }
                    skuInfos.sort(Comparator.comparing(SessionRelatedSkuInfoVO::getSeckillSort, Comparator.nullsLast(Integer::compareTo))
                            .thenComparing(SessionRelatedSkuInfoVO::getSkuId, Comparator.nullsLast(Long::compareTo)));
                    sessionVO.setSkuInfos(skuInfos);
                }
            } else {
                sessionVO.setSkuInfos(List.of());
            }

            result.add(sessionVO);
        }

        result.sort(Comparator.comparing(SessionVO::getStartTime, Comparator.nullsLast(LocalDateTime::compareTo))
                .thenComparing(SessionVO::getId, Comparator.nullsLast(Long::compareTo)));
        return Collections.unmodifiableList(result);
    }
}
