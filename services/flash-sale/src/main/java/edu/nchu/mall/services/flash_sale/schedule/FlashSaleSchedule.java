package edu.nchu.mall.services.flash_sale.schedule;

import edu.nchu.mall.services.flash_sale.service.FlashSaleService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FlashSaleSchedule {

    public static final String CRON_EXPRESSION = "0 * * * * ?"; // 每天凌晨执行一次
    public static final String CRON_EXPRESSION_TEST = "0/10 * * * * ?"; // 每10秒执行一次（测试用）

    public static final String LOAD_FLASH_SALE_LOCK_KEY = "flash_sale:load_lock";

    @Autowired
    FlashSaleService flashSaleService;

    @Autowired
    RedissonClient redissonClient;

    @Scheduled(cron = CRON_EXPRESSION)
    public void loadFlashSaleSessions() {
        log.info("Starting to load flash sale sessions into Redis");

        RLock lock = redissonClient.getLock(LOAD_FLASH_SALE_LOCK_KEY);

        try {
            lock.lock();
            flashSaleService.uploadFlashSaleSkusToRedis_3d();
        } catch (Exception e) {
            log.error("Failed to load flash sale sessions into Redis", e);
        } finally {
            lock.unlock();
        }
    }
}
