package edu.nchu.mall.models.annotation.notice;

import edu.nchu.mall.models.notice.event.Event;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Notice {
    Class<? extends Event> event();

    Advice advice() default Advice.AFTER;

    enum Advice {
        BEFORE, AFTER, EXCEPTION
    }
}
