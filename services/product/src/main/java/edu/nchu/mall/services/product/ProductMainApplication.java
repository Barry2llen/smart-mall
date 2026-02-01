package edu.nchu.mall.services.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication(
        scanBasePackages = "edu.nchu.mall.components",
        scanBasePackageClasses = {ProductMainApplication.class}
)
@EnableFeignClients(
        basePackages = {"edu.nchu.mall.components.feign.file", "edu.nchu.mall.components.feign.coupon"},
        basePackageClasses = {ProductMainApplication.class}
)
@EnableCaching
// 暴露当前代理对象，允许在业务代码中使用 AopContext.currentProxy()（用于解决自调用场景下的缓存/事务等 AOP 不生效问题）
@EnableAspectJAutoProxy(exposeProxy = true, proxyTargetClass = true)
public class ProductMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductMainApplication.class, args);
    }
}
