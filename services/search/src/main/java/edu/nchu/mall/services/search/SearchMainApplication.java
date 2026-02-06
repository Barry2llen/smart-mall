package edu.nchu.mall.services.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class SearchMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(SearchMainApplication.class, args);
    }
}
