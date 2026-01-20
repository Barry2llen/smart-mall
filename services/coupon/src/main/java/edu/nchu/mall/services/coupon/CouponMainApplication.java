package edu.nchu.mall.services.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(
        scanBasePackages = "edu.nchu.mall.components",
        scanBasePackageClasses = {CouponMainApplication.class}
)
@EnableCaching
public class CouponMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(CouponMainApplication.class, args);
    }
}
