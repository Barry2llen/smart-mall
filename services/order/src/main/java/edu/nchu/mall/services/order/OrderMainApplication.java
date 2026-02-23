package edu.nchu.mall.services.order;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableRabbit
@EnableCaching
@SpringBootApplication(
        scanBasePackages = "edu.nchu.mall.components",
        scanBasePackageClasses = {OrderMainApplication.class}
)
@EnableFeignClients(
        basePackages = {
                "edu.nchu.mall.components.feign.cart",
                "edu.nchu.mall.components.feign.member"
        },
        basePackageClasses = {OrderMainApplication.class}
)
@EnableAspectJAutoProxy(exposeProxy = true, proxyTargetClass = true)
public class OrderMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderMainApplication.class, args);
    }
}
