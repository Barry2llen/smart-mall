package edu.nchu.mall.components.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import edu.nchu.mall.components.config.LocalCacheTtlProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.transaction.AbstractTransactionSupportingCacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class TwoLevelCacheManager extends AbstractTransactionSupportingCacheManager {

    private final RedisCacheManager redisCacheManager;
    private final LocalCacheTtlProperties localCacheTtlProperties;
    private final boolean allowNullValues;
    private final Set<String> initialCacheNames;

    public TwoLevelCacheManager(RedisCacheManager redisCacheManager,
                                LocalCacheTtlProperties localCacheTtlProperties,
                                boolean allowNullValues,
                                Collection<String> initialCacheNames) {
        this.redisCacheManager = redisCacheManager;
        this.localCacheTtlProperties = localCacheTtlProperties;
        this.allowNullValues = allowNullValues;
        this.initialCacheNames = new LinkedHashSet<>(initialCacheNames);
    }

    @Override
    protected Collection<? extends Cache> loadCaches() {
        return initialCacheNames.stream()
                .map(this::createCache)
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    @Override
    protected Cache getMissingCache(String name) {
        return createCache(name);
    }

    private Cache createCache(String name) {
        Cache redisCache = redisCacheManager.getCache(name);
        if (redisCache == null) {
            return null;
        }

        return new TwoLevelCache(
                name,
                redisCache,
                Caffeine.newBuilder()
                        .expireAfterWrite(localCacheTtlProperties.resolve(name))
                        .build(),
                allowNullValues
        );
    }
}
