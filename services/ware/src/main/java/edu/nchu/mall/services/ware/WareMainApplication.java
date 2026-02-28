package edu.nchu.mall.services.ware;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableCaching
@EnableRabbit
@SpringBootApplication(
        scanBasePackages = "edu.nchu.mall.components",
        scanBasePackageClasses = {WareMainApplication.class}
)
@EnableFeignClients(basePackages = {
        "edu.nchu.mall.components.feign.order"
})
@EnableAspectJAutoProxy(exposeProxy = true, proxyTargetClass = true)
public class WareMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(WareMainApplication.class, args);
    }
}
