package edu.nchu.mall.services.flash_sale.config;

import edu.nchu.mall.components.config.ThreadPoolConfig;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig implements AsyncConfigurer {

    private final Executor executor;

    public AsyncConfig(@Qualifier(ThreadPoolConfig.VTHREAD_POOL_NAME) Executor e) {
        this.executor = e;
    }

    @Override
    public Executor getAsyncExecutor() {
        return this.executor;
    }

    // 还可以顺便定义异步任务异常处理
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }
}
