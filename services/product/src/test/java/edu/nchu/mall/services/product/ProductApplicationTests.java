package edu.nchu.mall.services.product;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.nchu.mall.components.config.ThreadPoolConfig;
import edu.nchu.mall.services.product.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@SpringBootTest
public class ProductApplicationTests {

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    UserService userService;

    @Autowired
    @Qualifier(ThreadPoolConfig.THREAD_POOL_NAME)
    Executor executor;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testVirtualThread() {
        for (int i = 0; i < 100; i++) {
            Thread.ofVirtual().start(() -> {
                log.info("Thread: {}", Thread.currentThread());
            });
        }
    }

    @Test
    void testFuture() {
        CompletableFuture<String> result = CompletableFuture.supplyAsync(() -> {
            log.info("Thread: {}", Thread.currentThread());
            int res = 0;
            for (int i = 1; i <= 100; i++) {
                res += i;
            }
            return res;
        }).thenApplyAsync(res -> {
            log.info("Thread: {}", Thread.currentThread());
            return String.valueOf(res);
        });

        log.info("Thread: {}", Thread.currentThread());
        log.info("Result: {}", result.join());
    }


}
