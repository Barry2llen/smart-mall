package edu.nchu.mall.services.cart;

import edu.nchu.mall.components.config.RedisCacheTtlConfig;
import edu.nchu.mall.components.config.ThreadPoolConfig;
import edu.nchu.mall.services.cart.config.CartRedisCacheTtlConfig;
import edu.nchu.mall.services.cart.service.CartService;
import edu.nchu.mall.models.vo.Cart;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@SpringBootTest
public class CartApplicationTests {
    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    @Qualifier(ThreadPoolConfig.VTHREAD_POOL_NAME)
    Executor executor;

    @Autowired
    CartService cartService;

    @Autowired
    RedisCacheTtlConfig redisCacheTtlConfig;

    @Test
    void contextLoads() {
        Map<Object, Object> entries = (redisTemplate.opsForHash().entries("test"));
        System.out.println(entries);
        entries.forEach((k ,v) -> {
            System.out.printf("Key [%s], Value [%s]\n", k , v);
            System.out.println(k instanceof String);
            System.out.println(v instanceof String);
        });
    }

    @Test
    void testVirtualThread() {
        CompletableFuture<Void> task = CompletableFuture.runAsync(() -> {
            log.info("Current thread: [{}]", Thread.currentThread());
        }, executor);
        Thread.ofVirtual().start(() -> {
            log.info("Virtual thread: [{}]", Thread.currentThread());
        });
    }

    @Test
    void testCartService() {
        Cart cart = cartService.getCart(1L);
        System.out.println(cart);
    }

    @Test
    void testRedisTtl() {
        System.out.println(redisCacheTtlConfig instanceof CartRedisCacheTtlConfig);
        System.out.println(redisCacheTtlConfig.getTimeToLive("cart-cache::123", new Object()).getSeconds());
    }
}
