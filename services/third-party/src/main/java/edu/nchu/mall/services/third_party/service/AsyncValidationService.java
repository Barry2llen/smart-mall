package edu.nchu.mall.services.third_party.service;

import java.util.concurrent.CompletableFuture;

public interface AsyncValidationService<T, V, R, RV> {
    CompletableFuture<R> send(T t);

    RV validate(V v);

    boolean retryable(T t);

    boolean exists(T t);
}
