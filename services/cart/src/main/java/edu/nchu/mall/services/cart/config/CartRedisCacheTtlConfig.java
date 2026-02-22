package edu.nchu.mall.services.cart.config;

import edu.nchu.mall.components.config.RedisCacheTtlConfig;
import edu.nchu.mall.services.cart.constants.RedisConstant;
import io.netty.util.internal.ThreadLocalRandom;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RefreshScope
public class CartRedisCacheTtlConfig extends RedisCacheTtlConfig {

    @Value("${cache.ttl.cart-cache-ms:1000}")
    private Long cartCacheTtl;

    @Override
    public Duration getTimeToLive(Object key, Object value) {
        if (key instanceof String s && s.startsWith(RedisConstant.MICRO_CACHE_MARK)) {
            return Duration.ofMillis(ThreadLocalRandom.current().nextLong(cartCacheTtl)).plus(Duration.ofMillis(cartCacheTtl));
        }
        return super.getTimeToLive(key, value);
    }
}
