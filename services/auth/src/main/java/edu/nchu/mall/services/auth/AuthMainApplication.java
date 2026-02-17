package edu.nchu.mall.services.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(
        basePackages = {"edu.nchu.mall.components.feign.third_party", "edu.nchu.mall.components.feign.member"},
        basePackageClasses = {AuthMainApplication.class}
)
@SpringBootApplication(
        scanBasePackages = "edu.nchu.mall.components",
        scanBasePackageClasses = {AuthMainApplication.class}
)
public class AuthMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthMainApplication.class, args);
    }
}
