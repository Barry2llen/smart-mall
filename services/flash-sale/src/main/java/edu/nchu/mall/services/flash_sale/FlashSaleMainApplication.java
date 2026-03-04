package edu.nchu.mall.services.flash_sale;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableRabbit
@EnableScheduling
@EnableFeignClients(basePackages = {
        "edu.nchu.mall.components.feign.coupon",
        "edu.nchu.mall.components.feign.product"
})
@SpringBootApplication(
        scanBasePackages = "edu.nchu.mall.components",
        scanBasePackageClasses = FlashSaleMainApplication.class,
        exclude = DataSourceAutoConfiguration.class
)
public class FlashSaleMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(FlashSaleMainApplication.class, args);
    }
}
