package edu.nchu.mall.services.flash_sale.schedule;

import edu.nchu.mall.components.notice.event.impl.HelloEvent;
import edu.nchu.mall.models.annotation.notice.Notice;
import edu.nchu.mall.services.flash_sale.service.FlashSaleService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class FlashSaleSchedule {

    public static final String CRON_EXPRESSION = "0 0 0 * * ?"; // 每天凌晨执行一次
    public static final String CRON_EXPRESSION_TEST1 = "0/10 * * * * ?"; // 每10秒执行一次（测试用）
    public static final String CRON_EXPRESSION_TEST2 = "0 30 21 * * ?";

    public static final String LOAD_FLASH_SALE_LOCK_KEY = "flash_sale:load_lock";
    public static final String LOAD_FLASH_SALE_WINDOW_KEY_PREFIX = "flash_sale:load:window:";
    public static final int LOAD_FLASH_SALE_LOCK_LEASE_SECONDS = 120;
    public static final int LOAD_FLASH_SALE_WINDOW_TTL_SECONDS = 70;
    public static final DateTimeFormatter LOAD_WINDOW_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

    @Autowired
    FlashSaleService flashSaleService;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Scheduled(cron = CRON_EXPRESSION)
    public void loadFlashSaleSessions() {
        log.info("Starting to load flash sale sessions into Redis");

        RLock lock = redissonClient.getLock(LOAD_FLASH_SALE_LOCK_KEY);
        boolean locked = false;

        try {
            locked = lock.tryLock(0, LOAD_FLASH_SALE_LOCK_LEASE_SECONDS, TimeUnit.SECONDS);
            if (!locked) {
                log.info("Skip loading flash sale sessions because lock is held by another node");
                return;
            }

            String windowKey = LOAD_FLASH_SALE_WINDOW_KEY_PREFIX + LocalDateTime.now().format(LOAD_WINDOW_FORMATTER);
            Boolean firstInWindow = redisTemplate.opsForValue().setIfAbsent(
                    windowKey,
                    "1",
                    Duration.ofSeconds(LOAD_FLASH_SALE_WINDOW_TTL_SECONDS)
            );
            if (!Boolean.TRUE.equals(firstInWindow)) {
                log.info("Skip loading flash sale sessions because current window has already been processed, key={}", windowKey);
                return;
            }

            flashSaleService.uploadFlashSaleSkusToRedis_3d();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while trying to load flash sale sessions", e);
        } catch (Exception e) {
            log.error("Failed to load flash sale sessions into Redis", e);
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
