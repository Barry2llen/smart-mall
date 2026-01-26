package edu.nchu.shop.services.file;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class FileMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(FileMainApplication.class, args);
    }
}
