package edu.nchu.mall.components.utils;

import edu.nchu.mall.components.config.ThreadPoolConfig;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.models.model.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Component
public class CallTaskUtils{

    @Autowired
    @Qualifier(ThreadPoolConfig.VTHREAD_POOL_NAME)
    Executor executor;

    public <T> CompletableFuture<Try<T>> rcall(Supplier<R<T>> supplier) {
        return CompletableFuture.supplyAsync(() -> {
            R<T> res = supplier.get();
            if (res.getCode() != RCT.SUCCESS) {
                throw new RuntimeException();
            }
            return Try.success(res.getData());
        }, executor).exceptionally(Try::failure);
    }

    public <T> CompletableFuture<Try<T>> call(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(() -> Try.success(supplier.get()), executor).exceptionally(Try::failure);
    }

    public <T, K> Map<K, CompletableFuture<Try<T>>> rcall(List<K> ids, Supplier<R<T>>... suppliers) {
        if (ids == null || suppliers == null || ids.size() != suppliers.length) {
            throw new IllegalArgumentException();
        }
        Map<K, CompletableFuture<Try<T>>> map = new HashMap<>(ids.size());
        for (int i = 0;i < ids.size();i++) {
            int finalI = i;
            CompletableFuture<Try<T>> each = CompletableFuture.supplyAsync(() -> {
                R<T> res = suppliers[finalI].get();
                if (res.getCode() != RCT.SUCCESS) {
                    throw new RuntimeException();
                }
                return Try.success(res.getData());
            }, executor).exceptionally(Try::failure);
            map.put(ids.get(i), each);
        }
        return map;
    }

    public <T, K> Map<K, CompletableFuture<Try<T>>> call(List<K> ids, Supplier<T>... suppliers) {
        if (ids == null || suppliers == null || ids.size() != suppliers.length) {
            throw new IllegalArgumentException();
        }
        Map<K, CompletableFuture<Try<T>>> map = new HashMap<>(ids.size());
        for (int i = 0; i < ids.size(); i++) {
            int finalI = i;
            CompletableFuture<Try<T>> each = CompletableFuture.supplyAsync(() -> Try.success(suppliers[finalI].get()), executor).exceptionally(Try::failure);
            map.put(ids.get(i), each);
        }
        return map;
    }
}
