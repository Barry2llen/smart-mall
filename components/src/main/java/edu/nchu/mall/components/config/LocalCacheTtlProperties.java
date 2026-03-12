package edu.nchu.mall.components.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ConfigurationProperties(prefix = "cache.local-ttl")
public class LocalCacheTtlProperties {

    private long defaultMs = 1000L;

    private Map<String, Long> perCache = new HashMap<>();

    public Duration resolve(String cacheName) {
        Long ttlMs = perCache.get(cacheName);
        long resolved = ttlMs == null ? defaultMs : ttlMs;
        return Duration.ofMillis(Math.max(resolved, 0L));
    }
}
