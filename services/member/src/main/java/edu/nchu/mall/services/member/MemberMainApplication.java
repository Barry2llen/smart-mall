package edu.nchu.mall.services.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(
        scanBasePackages = "edu.nchu.mall.components",
        scanBasePackageClasses = {MemberMainApplication.class}
)
@EnableCaching
public class MemberMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(MemberMainApplication.class, args);
    }
}
