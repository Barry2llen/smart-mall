package edu.nchu.mall.components.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class ThreadPoolConfig {

    public final static String THREAD_POOL_NAME = "customThreadPoolExecutor";
    public final static String VTHREAD_POOL_NAME = "customVThreadPoolExecutor";
    @ConditionalOnMissingBean(name = THREAD_POOL_NAME)
    @Bean(THREAD_POOL_NAME)
    public Executor customThreadPoolExecutor() {
        return new ThreadPoolExecutor(
                12, 200, 10, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(10000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    @ConditionalOnMissingBean(name = VTHREAD_POOL_NAME)
    @Bean(VTHREAD_POOL_NAME)
    public Executor customVThreadPoolExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
