package edu.nchu.mall.services.flash_sale;

import edu.nchu.mall.services.flash_sale.schedule.FlashSaleSchedule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class FlashSaleTests {

    @Autowired
    FlashSaleSchedule flashSaleSchedule;

    @Test
    void contextLoads() {
        //flashSaleSchedule.test();
    }
}
