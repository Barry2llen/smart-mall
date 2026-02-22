package edu.nchu.mall.components.autoConfig;

import edu.nchu.mall.components.config.RedisCacheTtlConfig;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({CacheProperties.class})
public class RedisCacheTtlAutoConfiguration {

    @Bean
    @RefreshScope
    @ConditionalOnMissingBean(RedisCacheTtlConfig.class)
    public RedisCacheTtlConfig defaultRedisCacheTtlConfig(CacheProperties cacheProperties) {
        return new DefaultRedisCacheTtlConfig();
    }

    public static class DefaultRedisCacheTtlConfig extends RedisCacheTtlConfig {
    }
}
