package edu.nchu.mall.components.cache;

import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.cache.support.AbstractValueAdaptingCache;

import java.util.concurrent.Callable;

public class TwoLevelCache extends AbstractValueAdaptingCache {

    private static final int LOCK_STRIPES = 64;

    private final String name;
    private final org.springframework.cache.Cache redisCache;
    private final Cache<Object, Object> localCache;
    private final Object[] keyLocks = new Object[LOCK_STRIPES];

    public TwoLevelCache(String name,
                         org.springframework.cache.Cache redisCache,
                         Cache<Object, Object> localCache,
                         boolean allowNullValues) {
        super(allowNullValues);
        this.name = name;
        this.redisCache = redisCache;
        this.localCache = localCache;
        for (int i = 0; i < LOCK_STRIPES; i++) {
            keyLocks[i] = new Object();
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getNativeCache() {
        return localCache;
    }

    @Override
    protected Object lookup(Object key) {
        Object localValue = localCache.getIfPresent(key);
        if (localValue != null) {
            return localValue;
        }

        ValueWrapper redisValue = redisCache.get(key);
        if (redisValue == null) {
            return null;
        }

        Object storeValue = toStoreValue(redisValue.get());
        localCache.put(key, storeValue);
        return storeValue;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Object key, Callable<T> valueLoader) {
        ValueWrapper valueWrapper = get(key);
        if (valueWrapper != null) {
            return (T) valueWrapper.get();
        }

        synchronized (getLock(key)) {
            valueWrapper = get(key);
            if (valueWrapper != null) {
                return (T) valueWrapper.get();
            }

            T value;
            try {
                value = redisCache.get(key, valueLoader);
            } catch (ValueRetrievalException ex) {
                throw ex;
            } catch (RuntimeException ex) {
                throw new ValueRetrievalException(key, valueLoader, ex);
            }

            if (value != null || isAllowNullValues()) {
                localCache.put(key, toStoreValue(value));
            } else {
                localCache.invalidate(key);
            }
            return value;
        }
    }

    @Override
    public void put(Object key, Object value) {
        localCache.put(key, toStoreValue(value));
        redisCache.put(key, value);
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        synchronized (getLock(key)) {
            ValueWrapper current = get(key);
            if (current != null) {
                return current;
            }

            ValueWrapper existing = redisCache.putIfAbsent(key, value);
            if (existing == null) {
                if (value != null || isAllowNullValues()) {
                    localCache.put(key, toStoreValue(value));
                } else {
                    localCache.invalidate(key);
                }
                return null;
            }

            localCache.put(key, toStoreValue(existing.get()));
            return existing;
        }
    }

    @Override
    public void evict(Object key) {
        localCache.invalidate(key);
        redisCache.evict(key);
    }

    @Override
    public boolean evictIfPresent(Object key) {
        localCache.invalidate(key);
        return redisCache.evictIfPresent(key);
    }

    @Override
    public void clear() {
        localCache.invalidateAll();
        redisCache.clear();
    }

    @Override
    public boolean invalidate() {
        localCache.invalidateAll();
        return redisCache.invalidate();
    }

    private Object getLock(Object key) {
        return keyLocks[Math.floorMod(key.hashCode(), LOCK_STRIPES)];
    }
}
