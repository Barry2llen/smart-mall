package edu.nchu.mall.models.notice.event;

@FunctionalInterface
public interface Event {
    void handle(Object... args);
}
