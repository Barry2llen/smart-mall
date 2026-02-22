package edu.nchu.mall.services.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(
        scanBasePackages = "edu.nchu.mall.components",
        scanBasePackageClasses = {CartMainApplication.class}
)
@EnableFeignClients(
        basePackages = {
                "edu.nchu.mall.components.feign.product",
                "edu.nchu.mall.components.feign.ware"
        },
        basePackageClasses = {CartMainApplication.class}
)
@EnableCaching
@EnableAspectJAutoProxy(exposeProxy = true, proxyTargetClass = true)
public class CartMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(CartMainApplication.class, args);
    }
}
