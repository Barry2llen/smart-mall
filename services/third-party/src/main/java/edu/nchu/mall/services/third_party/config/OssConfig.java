package edu.nchu.mall.services.third_party.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import edu.nchu.mall.services.third_party.properties.OSSProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OssConfig {

    @Autowired
    private OSSProperties ossProperties;

    @Bean(destroyMethod = "shutdown")
    public OSS ossClient() {
        String endpoint = ossProperties.getOss().getEndpoint();
        String accessKey = ossProperties.getAccessKey();
        String secretKey = ossProperties.getSecretKey();
        return new OSSClientBuilder().build(endpoint, accessKey, secretKey);
    }
}
