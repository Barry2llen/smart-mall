package edu.nchu.mall.components.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class ThreadPoolConfig {

    public final static String THREAD_POOL_NAME = "customThreadPoolExecutor";
    @ConditionalOnMissingBean(name = THREAD_POOL_NAME)
    @Bean(THREAD_POOL_NAME)
    public Executor customThreadPoolExecutor() {
        Executor executor = new ThreadPoolExecutor(
                12, 200, 10, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(10000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );

        return executor;
    }
}
