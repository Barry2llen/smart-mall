package edu.nchu.mall.components.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.lang.Nullable;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

public abstract class RedisCacheTtlConfig implements RedisCacheWriter.TtlFunction {

    @Value("${cache.random-ttl-bound-ms:30000}")
    protected long RANDOM_TTL_BOUND_MS;

    @Value("${cache.null-ttl-ms:10000}")
    protected long NULL_TTL_MS;

    @Autowired
    protected CacheProperties cacheProperties;
    @Override
    public Duration getTimeToLive(Object key, @Nullable Object value) {
        if(value == null){
            return Duration.ofMillis(NULL_TTL_MS);
        }
        return cacheProperties.getRedis().getTimeToLive().plus(Duration.ofMillis(ThreadLocalRandom.current().nextLong(RANDOM_TTL_BOUND_MS)));
    }
}
