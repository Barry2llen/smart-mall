package edu.nchu.mall.services.flash_sale.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.nchu.mall.components.feign.coupon.CouponFeignClient;
import edu.nchu.mall.components.feign.member.MemberFeignClient;
import edu.nchu.mall.components.feign.product.ProductFeignClient;
import edu.nchu.mall.models.entity.MemberReceiveAddress;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.models.model.Try;
import edu.nchu.mall.models.to.mq.FlashSaleOrder;
import edu.nchu.mall.models.vo.SeckillSessionVO;
import edu.nchu.mall.models.vo.SeckillSkuRelationVO;
import edu.nchu.mall.models.vo.SkuInfoVO;
import edu.nchu.mall.services.flash_sale.dto.Kill;
import edu.nchu.mall.services.flash_sale.rentity.UserInfo;
import edu.nchu.mall.services.flash_sale.service.DelayMessageSender;
import edu.nchu.mall.services.flash_sale.service.FlashSaleService;
import edu.nchu.mall.services.flash_sale.rentity.FlashSaleCleanupMessage;
import edu.nchu.mall.services.flash_sale.rentity.FlashSaleRelatedSkuInfo;
import edu.nchu.mall.services.flash_sale.rentity.FlashSaleSession;
import edu.nchu.mall.services.flash_sale.constants.RedisConstant;
import edu.nchu.mall.services.flash_sale.vo.OrderConfirm;
import edu.nchu.mall.services.flash_sale.vo.OrderItem;
import edu.nchu.mall.services.flash_sale.vo.SessionRelatedSkuInfoVO;
import edu.nchu.mall.services.flash_sale.vo.SessionVO;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RefreshScope
public class FlashSaleServiceImpl implements FlashSaleService {

    @Value("${flashsale.cache.timeout-seconds:86400}") // 默认24小时
    private long flashSaleCacheTimeoutMs;

    @Autowired
    ProductFeignClient productFeignClient;

    @Autowired
    CouponFeignClient couponFeignClient;

    @Autowired
    MemberFeignClient memberFeignClient;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    DelayMessageSender delayMessageSender;

    @Autowired
    RabbitTemplate rabbitTemplate;

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
            final String sessionSkusKey = RedisConstant.FLASH_SALE_SESSION_SKUS_KEY_PREFIX + session.getId();
            final String newDigest = buildSessionDigest(session);
            final Object oldDigestObj = redisTemplate.opsForHash().get(RedisConstant.FLASH_SALE_SESSION_DIGEST_KEY, sessionIdStr);
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
                cleanupSemaphoresByHscan(session.getId().toString(), sessionSkusKey);
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
            redisTemplate.opsForZSet().add(RedisConstant.FLASH_SALE_SESSIONS_KEY, sessionIdStr, flashSaleSession.getEndTime().atZone(java.time.ZoneId.systemDefault()).toEpochSecond());
            redisTemplate.opsForHash().put(RedisConstant.FLASH_SALE_SESSIONS_INFO_KEY, sessionIdStr, sessionJson);

            skuInfos.forEach(each -> {
                // 每个参与秒杀的SKU都创建一个分布式信号量，信号量的permits数量就是秒杀库存数量
                RSemaphore semaphore = redissonClient.getSemaphore(RedisConstant.FLASH_SALE_SKU_SEMAPHORE_KEY_PREFIX + each.getRandomCode());
                semaphore.trySetPermits(each.getSeckillCount());

                // 创建sku与场次的关联，方便后续查询某个sku参与了哪些场次
                redisTemplate.opsForHash().put(RedisConstant.FLASH_SALE_SKU_SESSIONS_KEY_PREFIX + each.getSkuId(), sessionIdStr, "1");
            });

            redisTemplate.opsForHash().putAll(sessionSkusKey, skuInfos.stream().collect(Collectors.toMap(info -> info.getSkuId().toString(), info -> {
                try {
                    return mapper.writeValueAsString(info);
                } catch (Exception e) {
                    log.error("Failed to serialize SKU info for SKU {}: {}", info.getSkuId(), e.getMessage(), e);
                    return "{ \"error\": \"Failed to serialize SKU info\" }";
                }
            })));

            redisTemplate.opsForHash().put(RedisConstant.FLASH_SALE_SESSION_DIGEST_KEY, sessionIdStr, newDigest);

            LocalDateTime cleanTime = session.getEndTime().plusSeconds(flashSaleCacheTimeoutMs);
            //LocalDateTime cleanTime = LocalDateTime.now().plusSeconds(15L); // 测试用，15秒后清理缓存
            FlashSaleCleanupMessage cleanupMessage = new FlashSaleCleanupMessage(session.getId(), newDigest, cleanTime);
            redisTemplate.opsForHash().put(RedisConstant.FLASH_SALE_CLEANUP_META_KEY, sessionIdStr, mapper.writeValueAsString(cleanupMessage));
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

    /**
     * 通过HSCAN遍历某个场次的所有SKU信息，删除对应的库存信号量
     * @param sessionId 场次ID
     * @param sessionSkusKey 场次SKU信息在Redis中的key，即"flash_sale:session_skus:" + sessionId
     */
    private void cleanupSemaphoresByHscan(String sessionId, String sessionSkusKey) {
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
                    redissonClient.getSemaphore(RedisConstant.FLASH_SALE_SKU_SEMAPHORE_KEY_PREFIX + randomCode).delete();

                    // 顺便删除sku与场次的关联
                    cleanupSkuSessionsByHscan(skuInfo.getSkuId().toString(), sessionId);
                } catch (Exception e) {
                    log.error("Failed to process SKU info during cache cleanup for session {} and skuField {}: {}", sessionId, entry.getKey(), e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public void cleanFlashSaleSessionCache(String sessionId) {
        // 删除场次信息
        redisTemplate.opsForHash().delete(RedisConstant.FLASH_SALE_SESSIONS_INFO_KEY, sessionId);
        // 删除库存信号量
        final String sessionSkusKey = RedisConstant.FLASH_SALE_SESSION_SKUS_KEY_PREFIX + sessionId;
        cleanupSemaphoresByHscan(sessionId, sessionSkusKey);
        // 删除商品信息
        redisTemplate.unlink(sessionSkusKey);
        // 从ZSet中删除场次ID
        redisTemplate.opsForZSet().remove(RedisConstant.FLASH_SALE_SESSIONS_KEY, sessionId);
        redisTemplate.opsForHash().delete(RedisConstant.FLASH_SALE_SESSION_DIGEST_KEY, sessionId);
        redisTemplate.opsForHash().delete(RedisConstant.FLASH_SALE_CLEANUP_META_KEY, sessionId);
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
                ? redisTemplate.opsForZSet().range(RedisConstant.FLASH_SALE_SESSIONS_KEY, offset, offset + safePageSize - 1L)
                : redisTemplate.opsForZSet().rangeByScore(RedisConstant.FLASH_SALE_SESSIONS_KEY, nowEpochSecond, Double.POSITIVE_INFINITY, offset, safePageSize);
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
        List<Object> sessionJsons = redisTemplate.opsForHash().multiGet(RedisConstant.FLASH_SALE_SESSIONS_INFO_KEY, sessionIdFields);
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
                String sessionSkusKey = RedisConstant.FLASH_SALE_SESSION_SKUS_KEY_PREFIX + flashSaleSession.getId();
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
                            SessionRelatedSkuInfoVO skuInfoVO = skuInfo2VO(flashSaleSession, skuInfo);
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

    @Override
    public boolean isSkuInFlashSale(Long userId, Long skuId) {
        boolean res =  redisTemplate.opsForHash().size(RedisConstant.FLASH_SALE_SKU_SESSIONS_KEY_PREFIX + skuId) > 0;
        if (res) {
            preCacheUserInfo(userId);
        }
        return res;
    }

    @Override
    public List<FlashSaleSession> getFlashSaleSessionsBySkuId(Long skuId) {
        // 这里直接一次性获取所有sessionId，因为应该不会有一个SKU参与非常多的场次
        List<Object> sessionIds = redisTemplate.opsForHash().keys(RedisConstant.FLASH_SALE_SKU_SESSIONS_KEY_PREFIX + skuId).stream().toList();
        if (sessionIds.isEmpty()) {
            return List.of();
        }

        List<Object> sessionJsons = redisTemplate.opsForHash().multiGet(RedisConstant.FLASH_SALE_SESSIONS_INFO_KEY, sessionIds);
        if (sessionJsons.isEmpty()) {
            return List.of();
        }

        List<FlashSaleSession> sessions = new ArrayList<>();
        sessionJsons.forEach(json -> {
            FlashSaleSession flashSaleSession = null;
            try {
                flashSaleSession = mapper.readValue(json.toString(), FlashSaleSession.class);
            } catch (JsonProcessingException e) {
                log.error("Failed to parse flash sale session JSON for SKU {}, json {}: {}", skuId, json, e.getMessage(), e);
            }

            if (flashSaleSession != null) {
                sessions.add(flashSaleSession);
            }
        });

        return sessions;
    }

    @Override
    public SessionVO getSessionById(Long sessionId, Boolean withProducts) {

        // 拿到session信息
        Object sessionJsonObj = redisTemplate.opsForHash().get(RedisConstant.FLASH_SALE_SESSIONS_INFO_KEY, sessionId.toString());
        if (!(sessionJsonObj instanceof String s)) {
            return null;
        }

        FlashSaleSession session = null;
        try {
            session = mapper.readValue(s, FlashSaleSession.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse flash sale session JSON for sessionId {}, json {}: {}", sessionId, s, e.getMessage(), e);
            return null;
        }

        SessionVO sessionVO = new SessionVO();
        sessionVO.setSkuInfos(new ArrayList<>());
        BeanUtils.copyProperties(session, sessionVO);

        if (!Boolean.TRUE.equals(withProducts)) {
            return sessionVO;
        }

        ScanOptions scanOptions = ScanOptions.scanOptions().count(200).build();
        final String sessionSkusKey = RedisConstant.FLASH_SALE_SESSION_SKUS_KEY_PREFIX + sessionId;
        try (Cursor<Map.Entry<Object, Object>> cursor = redisTemplate.opsForHash().scan(sessionSkusKey, scanOptions)) {
            while (cursor.hasNext()) {
                Map.Entry<Object, Object> entry = cursor.next();
                try {
                    FlashSaleRelatedSkuInfo skuInfo = mapper.readValue((String) entry.getValue(), FlashSaleRelatedSkuInfo.class);
                    SessionRelatedSkuInfoVO skuInfoVO = skuInfo2VO(session, skuInfo);
                    sessionVO.getSkuInfos().add(skuInfoVO);
                } catch (Exception e) {
                    log.error("Failed to parse flash sale SKU JSON for sessionId {}, entry {}: {}", sessionId, entry, e.getMessage(), e);
                }
            }
        }

        return sessionVO;
    }

    @Override
    public OrderConfirm confirmOrder(Long userId, Long sessionId, Long skuId, int num) {
        // 获取对应商品sku信息
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(RedisConstant.FLASH_SALE_SESSION_SKUS_KEY_PREFIX + sessionId);
        String skuJson = hashOps.get(skuId.toString());

        if (skuJson == null || skuJson.isBlank()) {
            return null;
        }

        FlashSaleRelatedSkuInfo skuInfo = null;
        try {
            skuInfo = mapper.readValue(skuJson, FlashSaleRelatedSkuInfo.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse flash sale SKU JSON for kill request, sessionId {}, skuId {}, json {}: {}", sessionId, skuId, skuJson, e.getMessage(), e);
            return null;
        }

        // 用户信息
        String userInfoJson = redisTemplate.opsForValue().get(RedisConstant.FLASH_SALE_USER_INFO_KEY_PREFIX + userId);
        if (userInfoJson == null || userInfoJson.isBlank()) {
            return null;
        }

        UserInfo userInfo = null;
        try {
            userInfo = mapper.readValue(userInfoJson, UserInfo.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse user info JSON for kill request, userId {}, json {}: {}", userId, userInfoJson, e.getMessage(), e);
            return null;
        }

        OrderItem item = new OrderItem();
        BeanUtils.copyProperties(skuInfo, item);
        item.setTitle(skuInfo.getSkuTitle());
        item.setImage(skuInfo.getSkuDefaultImg());
        item.setPrice(skuInfo.getSeckillPrice());
        item.setCount(num);

        OrderConfirm confirm = new OrderConfirm();
        confirm.setAddresses(userInfo.getAddresses());
        confirm.setItem(item);

        return confirm;
    }

    @Override
    public KillStatus kill(Long userId, Kill dto) {

        Long sessionId = dto.getSessionId();
        Long skuId = dto.getSkuId();
        String randomCode = dto.getRandomCode();
        int num = dto.getNum();

        // 获取场次信息
        FlashSaleSession session = null;
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(RedisConstant.FLASH_SALE_SESSIONS_INFO_KEY);
        String sessionJson = hashOps.get(sessionId.toString());
        if (sessionJson == null || sessionJson.isBlank()) {
            return KillStatus.INVALID;
        }

        try {
            session = mapper.readValue(sessionJson, FlashSaleSession.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse flash sale session JSON for kill request, sessionId {}, json {}: {}", sessionId, sessionJson, e.getMessage(), e);
            return KillStatus.ERROR;
        }

        // 检验活动时间
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(session.getEndTime())) {
            return KillStatus.ENDED;
        } else if (now.isBefore(session.getStartTime())) {
            return KillStatus.NOT_STARTED;
        }

        // 获取对应商品sku信息
        hashOps = redisTemplate.boundHashOps(RedisConstant.FLASH_SALE_SESSION_SKUS_KEY_PREFIX + sessionId);
        String skuJson = hashOps.get(skuId.toString());

        if (skuJson == null || skuJson.isBlank()) {
            return KillStatus.INVALID;
        }

        FlashSaleRelatedSkuInfo skuInfo = null;
        try {
            skuInfo = mapper.readValue(skuJson, FlashSaleRelatedSkuInfo.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse flash sale SKU JSON for kill request, sessionId {}, skuId {}, json {}: {}", sessionId, skuId, skuJson, e.getMessage(), e);
            return KillStatus.ERROR;
        }

        // 检验随机码
        if (!skuInfo.getRandomCode().equals(randomCode)) {
            return KillStatus.INVALID;
        }

        if (num > skuInfo.getSeckillLimit()) {
            return KillStatus.LIMIT_EXCEEDED;
        }

        // 检验用户信息
        String userInfoJson = redisTemplate.opsForValue().get(RedisConstant.FLASH_SALE_USER_INFO_KEY_PREFIX + userId);
        if (userInfoJson == null || userInfoJson.isBlank()) {
            // 如果用户信息不存在，说明用户没有访问过秒杀相关接口，可能是恶意请求或者缓存过期了，直接拒绝请求
            return KillStatus.OPERATION_TOO_FREQUENT;
        }

        UserInfo userInfo = null;
        try {
            userInfo = mapper.readValue(userInfoJson, UserInfo.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse user info JSON for kill request, userId {}, json {}: {}", userId, userInfoJson, e.getMessage(), e);
            return KillStatus.ERROR;
        }

        if (dto.getAddressId() != null && userInfo.getAddresses().stream().map(MemberReceiveAddress::getId).noneMatch(addrId -> addrId.equals(dto.getAddressId()))) {
            // 如果用户提供的地址ID不在用户的地址列表中，说明可能是恶意请求，直接拒绝
            return KillStatus.INVALID;
        }

        // 锁住用户购买资格，防止同一用户发起大量请求
        RLock lock = redissonClient.getLock(RedisConstant.getUserLockKey(userId, sessionId, skuId));
        boolean lockAcquired = false;
        try {
            lockAcquired = lock.tryLock();
            if (!lockAcquired) {
                return KillStatus.INVALID;
            }

            // 检查用户购买记录，防止超过购买限制
            String userPurchaseKey = RedisConstant.getUserPurchaseKey(userId, sessionId, skuId);
            String cnt = redisTemplate.opsForValue().get(userPurchaseKey);
            if (cnt != null && Integer.parseInt(cnt) + num > skuInfo.getSeckillLimit()) {
                return KillStatus.LIMIT_EXCEEDED;
            }

            // 检查剩余库存，尝试获取信号量
            RSemaphore semaphore = redissonClient.getSemaphore(RedisConstant.FLASH_SALE_SKU_SEMAPHORE_KEY_PREFIX + randomCode);
            boolean permitsAcquired = semaphore.tryAcquire(num);
            if (!permitsAcquired) {
                return KillStatus.STOCK_NOT_ENOUGH;
            }

            // 记录购买结果
            long ttl = LocalDateTime.now().until(session.getEndTime(), ChronoUnit.MILLIS);
            if (cnt == null) {
                redisTemplate.opsForValue().set(userPurchaseKey, String.valueOf(num), ttl, TimeUnit.MILLISECONDS);
            } else {
                redisTemplate.opsForValue().increment(userPurchaseKey, num);
            }

            String orderSn = IdWorker.getTimeId();
            FlashSaleOrder order = new FlashSaleOrder();
            BeanUtils.copyProperties(skuInfo, order);
            order.setOrderSn(orderSn);
            order.setUserId(userId);
            order.setNum(num);
            order.setSessionId(sessionId);
            order.setRandomCode(skuInfo.getRandomCode());
            order.setPrice(skuInfo.getSeckillPrice());
            order.setAddressId(dto.getAddressId());
            order.setNote(dto.getNote());
            ORDER.set(order);

            rabbitTemplate.convertAndSend("order.event.exchange", "order.flashsale.create", order);

        } catch (AmqpException e) {
            log.error("Failed to send flash sale order creation message to RabbitMQ for user {} on session {} and sku {}, this may cause duplicate order creation: {}", userId, sessionId, skuId, e.getMessage(), e);
            return KillStatus.ERROR;
        } catch (Throwable e) {
            log.error("Unexpected error occurred during flash sale kill process for user {} on session {} and sku {}, this may cause duplicate order creation: {}", userId, sessionId, skuId, e.getMessage(), e);
            return KillStatus.ERROR;
        } finally {
            if (lockAcquired && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

        return KillStatus.SUCCEEDED;
    }

    public void deleteUserPurchaseRecord(Long userId, Long sessionId, Long skuId, String randomCode, int num) {
        redisTemplate.delete(RedisConstant.getUserPurchaseKey(userId, sessionId, skuId));
        redissonClient.getSemaphore(RedisConstant.FLASH_SALE_SKU_SEMAPHORE_KEY_PREFIX + randomCode).release(num);
    }

    @Override
    public Thread preCacheUserInfo(Long userId) {
        // 新开一个线程异步执行，避免阻塞当前线程
        return Thread.ofVirtual().start(() -> {
            Boolean exists = redisTemplate.hasKey(RedisConstant.FLASH_SALE_USER_INFO_KEY_PREFIX + userId);
            if (!exists) {
                cacheUserInfo(userId);
            }
        });
    }

    @Override
    public Thread reCacheUserInfo(Long userId) {
        // 新开一个线程异步执行，避免阻塞当前线程
        return Thread.ofVirtual().start(() -> {
            Boolean exists = redisTemplate.hasKey(RedisConstant.FLASH_SALE_USER_INFO_KEY_PREFIX + userId);
            if (exists) {
                cacheUserInfo(userId);
            }
        });
    }

    /**
     * 从会员服务查询用户信息并缓存到Redis中，缓存有效期为1天
     * @param userId 用户ID
     */
    private void cacheUserInfo(Long userId) {
        var rTry = Try.of(memberFeignClient::getMemberReceiveAddress, userId);
        if (rTry.failed() || rTry.getValue().getCode() != RCT.SUCCESS) {
            log.error("Failed to fetch user receive address for user {}: {}", userId, rTry.getValue() == null ? rTry.getEx().getMessage() : rTry.getValue().getMsg(), rTry.getEx());
            return;
        }

        UserInfo info = new UserInfo();
        info.setAddresses(rTry.getValue().getData());

        try {
            String json = mapper.writeValueAsString(info);
            redisTemplate.opsForValue().set(RedisConstant.FLASH_SALE_USER_INFO_KEY_PREFIX + userId, json, 1, TimeUnit.DAYS);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize user info for user {}: {}", userId, e.getMessage(), e);
        }
    }

    /**
     * 删除sku与场次的关联
     * @param skuId skuId
     * @param sessionId 场次id
     */
    private void cleanupSkuSessionsByHscan(String skuId, String sessionId) {
        redisTemplate.opsForHash().delete(RedisConstant.FLASH_SALE_SKU_SESSIONS_KEY_PREFIX + skuId, sessionId);
    }

    private SessionRelatedSkuInfoVO skuInfo2VO(FlashSaleSession session, FlashSaleRelatedSkuInfo info) {
        SessionRelatedSkuInfoVO skuInfoVO = new SessionRelatedSkuInfoVO();
        BeanUtils.copyProperties(info, skuInfoVO);
        if (!session.getStartTime().isBefore(LocalDateTime.now())) {
            skuInfoVO.setRandomCode(null); // 如果活动未开始，不返回随机码
        }
        return skuInfoVO;
    }
}
