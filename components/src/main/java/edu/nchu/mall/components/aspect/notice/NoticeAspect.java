package edu.nchu.mall.components.aspect.notice;

import edu.nchu.mall.models.annotation.notice.Notice;
import edu.nchu.mall.models.notice.event.Event;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class NoticeAspect implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Around("@annotation(notice)")
    public Object doNotice(ProceedingJoinPoint joinPoint, Notice notice) throws Throwable {
        Class<? extends Event> event = notice.event();
        Notice.Advice advice = notice.advice();

        Event bean = null;
        try {
            bean = applicationContext.getBean(event);
        } catch (BeansException e) {
            throw new RuntimeException("事件 " + event.getName() + " 未找到对应的处理器");
        }

        if (advice == Notice.Advice.BEFORE) {
            bean.handle(joinPoint.getArgs());
        }

        Object res = null;
        try {
            res = joinPoint.proceed();
        } catch (Throwable throwable) {
            if (advice == Notice.Advice.EXCEPTION) {
                bean.handle(joinPoint.getArgs());
            }
            throw throwable;
        }

        if (advice == Notice.Advice.AFTER) {
            bean.handle(joinPoint.getArgs());
        }

        return res;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
