package edu.nchu.mall.components.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
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
import java.util.Collection;
import java.util.Map;
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
        ObjectMapper objectMapper = new ObjectMapper();
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType("edu.nchu.mall")
                .allowIfSubType("java.util")
                .allowIfSubType("java.lang")
                .allowIfSubType("java.time")
                .allowIfSubType("java.math")
                .build();
        ObjectMapper.DefaultTypeResolverBuilder typeResolverBuilder =
                new ObjectMapper.DefaultTypeResolverBuilder(ObjectMapper.DefaultTyping.NON_FINAL, ptv) {
                    @Override
                    public boolean useForType(JavaType javaType) {
                        if (javaType.isJavaLangObject()) {
                            return true;
                        }
                        Class<?> rawClass = javaType.getRawClass();
                        if (rawClass.isArray()
                                || Collection.class.isAssignableFrom(rawClass)
                                || Map.class.isAssignableFrom(rawClass)) {
                            return true;
                        }
                        return super.useForType(javaType);
                    }
                };
        typeResolverBuilder.init(JsonTypeInfo.Id.CLASS, null);
        typeResolverBuilder.inclusion(JsonTypeInfo.As.PROPERTY);
        objectMapper.setDefaultTyping(typeResolverBuilder);
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));

        CacheProperties.Redis redisProperties = cacheProperties.getRedis();

        if (redisProperties.getTimeToLive() != null) {
            config = config.entryTtl((key, value) -> {
                if(value == null){
                    return Duration.ofMillis(NULL_TTL_MS);
                }
                return redisProperties.getTimeToLive().plus(Duration.ofMillis(ThreadLocalRandom.current().nextLong(RANDOM_TTL_BOUND_MS)));
            });
        }else{
            log.warn("Redis cache default TTL is not set, it is recommended to set a reasonable TTL to avoid cache avalanche.");
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
