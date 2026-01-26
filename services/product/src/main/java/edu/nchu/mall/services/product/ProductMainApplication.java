package edu.nchu.mall.services.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication(
        scanBasePackages = "edu.nchu.mall.components",
        scanBasePackageClasses = {ProductMainApplication.class}
)
@EnableFeignClients(
        basePackages = "edu.nchu.mall.components.feign",
        basePackageClasses = {ProductMainApplication.class}
)
@EnableCaching
public class ProductMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductMainApplication.class, args);
    }
}
