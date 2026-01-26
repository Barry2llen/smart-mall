package edu.nchu.shop.services.file.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("alibaba.cloud")
public class OSSProperties {
    private String accessKey;
    private String secretKey;
    private String arn;

    private OSS oss = new OSS();

    @Data
    public static class OSS{
        private String endpoint;
        private String bucket;
        private String region;
        private String host;
    }
}
