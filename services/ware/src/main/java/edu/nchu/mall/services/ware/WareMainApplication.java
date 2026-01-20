package edu.nchu.mall.services.ware;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(
        scanBasePackages = "edu.nchu.mall.components",
        scanBasePackageClasses = {WareMainApplication.class}
)
@EnableCaching
public class WareMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(WareMainApplication.class, args);
    }
}
