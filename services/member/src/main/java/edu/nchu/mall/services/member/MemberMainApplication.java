package edu.nchu.mall.services.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(
        scanBasePackages = "edu.nchu.mall.components",
        scanBasePackageClasses = {MemberMainApplication.class}
)
@EnableCaching
@EnableAspectJAutoProxy(exposeProxy = true, proxyTargetClass = true)
public class MemberMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(MemberMainApplication.class, args);
    }
}
