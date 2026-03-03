package edu.nchu.mall.services.third_party;

import edu.nchu.mall.components.config.ThreadPoolConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@Import(ThreadPoolConfig.class)
@SpringBootApplication(
        scanBasePackageClasses = ThirdPartyMainApplication.class,
        scanBasePackages = "edu.nchu.mall.components.utils",
        exclude = DataSourceAutoConfiguration.class
)
public class ThirdPartyMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(ThirdPartyMainApplication.class, args);
    }
}
