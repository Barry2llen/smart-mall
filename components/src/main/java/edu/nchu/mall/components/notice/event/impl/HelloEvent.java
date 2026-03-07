package edu.nchu.mall.components.notice.event.impl;

import edu.nchu.mall.models.notice.event.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
//@Component
public class HelloEvent implements Event {
    @Override
    public void handle(Object... args) {
        log.info("HelloEvent handled!");
    }
}
