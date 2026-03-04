package edu.nchu.mall.services.flash_sale.schedule;

import edu.nchu.mall.services.flash_sale.service.FlashSaleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FlashSaleSchedule {
    @Autowired
    FlashSaleService flashSaleService;

    @Scheduled(cron = "0 * * * * ?") // 每天凌晨执行一次
    public void loadFlashSaleSessions() {
        try {
            log.info("Starting to load flash sale sessions into Redis");
            flashSaleService.uploadFlashSaleSkusToRedis_3d();
        } catch (Exception e) {
            log.error("Failed to load flash sale sessions into Redis", e);
        }
    }
}
