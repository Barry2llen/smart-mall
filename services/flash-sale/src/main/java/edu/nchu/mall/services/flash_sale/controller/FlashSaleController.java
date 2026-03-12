package edu.nchu.mall.services.flash_sale.controller;

import edu.nchu.mall.services.flash_sale.schedule.FlashSaleSchedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 仅测试用，实际项目中不应该暴露这个接口
 */
//@RestController
public class FlashSaleController {

    //@Autowired
    FlashSaleSchedule schedule;

    @RequestMapping("/test/load")
    public String testLoad() {
        schedule.loadFlashSaleSessions();
        return "ok";
    }
}
