package edu.nchu.mall.services.third_party;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class ThirdPartyMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(ThirdPartyMainApplication.class, args);
    }
}
