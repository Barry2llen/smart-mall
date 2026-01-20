package edu.nchu.mall.components.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.support.NullValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Configuration
@EnableConfigurationProperties({CacheProperties.class})
public class CacheConfig {

    @Value("${cache.random-ttl-bound-ms:30000}")
    private long RANDOM_TTL_BOUND_MS;

    @Value("${cache.null-ttl-ms:10000}")
    private long NULL_TTL_MS;

    @Bean
    public RedisCacheConfiguration redisConfiguration(CacheProperties cacheProperties){
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        CacheProperties.Redis redisProperties = cacheProperties.getRedis();

        if (redisProperties.getTimeToLive() != null) {
            config = config.entryTtl((key, value) -> {
                log.warn("testing");
                if(value == null){
                    return Duration.ofMillis(NULL_TTL_MS);
                }
                return redisProperties.getTimeToLive().plus(Duration.ofMillis(ThreadLocalRandom.current().nextLong(RANDOM_TTL_BOUND_MS)));
            });
        }

        if (redisProperties.getKeyPrefix() != null) {
            config = config.prefixCacheNameWith(redisProperties.getKeyPrefix());
        }

        if (!redisProperties.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }

        if (!redisProperties.isUseKeyPrefix()) {
            config = config.disableKeyPrefix();
        }

        return config;
    }
}
