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
import edu.nchu.mall.services.flash_sale.vo.FlashSaleRelatedSkuInfo;
import edu.nchu.mall.services.flash_sale.vo.FlashSaleSession;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RefreshScope
public class FlashSaleServiceImpl implements FlashSaleService {

    public static final String FLASH_SALE_SESSIONS_KEY = "flash_sale:sessions";
    public static final String FLASH_SALE_SESSIONS_INFO_KEY = "flash_sale:sessions_info"; // 存储sessionId到FlashSaleSession的映射，方便查询
    public static final String FLASH_SALE_SESSION_SKUS_KEY_PREFIX = "flash_sale:session_skus:"; // 后面跟sessionId

    public static final String FLASH_SALE_SKU_SEMAPHORE_KEY_PREFIX = "flash_sale:semaphore:"; // 后面跟随机码，存储每个SKU的库存信号量

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
            List<SeckillSkuRelationVO> skuRelations = session.getRelations();
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
            redisTemplate.opsForZSet().add(FLASH_SALE_SESSIONS_KEY, session.getId().toString(), flashSaleSession.getEndTime().atZone(java.time.ZoneId.systemDefault()).toEpochSecond());
            redisTemplate.opsForHash().put(FLASH_SALE_SESSIONS_INFO_KEY, session.getId().toString(), sessionJson);

            skuInfos.forEach(each -> {
                RSemaphore semaphore = redissonClient.getSemaphore(FLASH_SALE_SKU_SEMAPHORE_KEY_PREFIX + each.getRandomCode());
                semaphore.trySetPermits(each.getSeckillCount());
            });

            String sessionSkusKey = FLASH_SALE_SESSION_SKUS_KEY_PREFIX + session.getId();
            redisTemplate.opsForHash().putAll(sessionSkusKey, skuInfos.stream().collect(Collectors.toMap(info -> info.getSkuId().toString(), info -> {
                try {
                    return mapper.writeValueAsString(info);
                } catch (Exception e) {
                    log.error("Failed to serialize SKU info for SKU {}: {}", info.getSkuId(), e.getMessage(), e);
                    return "{ \"error\": \"Failed to serialize SKU info\" }";
                }
            })));

            //delayMessageSender.sendDelayMessage(session.getId(), session.getEndTime().plusSeconds(flashSaleCacheTimeoutMs));
        }
    }

    @Override
    public void cleanFlashSaleSessionCache(Long sessionId) {
        // 删除商品信息
        String sessionSkusKey = FLASH_SALE_SESSION_SKUS_KEY_PREFIX + sessionId;
        redisTemplate.unlink(sessionSkusKey);
        // 删除场次信息
        redisTemplate.opsForHash().delete(FLASH_SALE_SESSIONS_INFO_KEY, sessionId);
        // 从ZSet中删除场次ID
        redisTemplate.opsForZSet().remove(FLASH_SALE_SESSIONS_KEY, sessionId);
    }
}
