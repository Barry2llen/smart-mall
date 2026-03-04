package edu.nchu.mall.services.flash_sale.service.impl;

import edu.nchu.mall.services.flash_sale.service.DelayMessageSender;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class DelayMessageSenderImpl implements DelayMessageSender {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    public void sendDelayMessage(Object msg, LocalDateTime time) {
        final long delayMillis = java.time.Duration.between(LocalDateTime.now(), time).toMillis();
        rabbitTemplate.convertAndSend(
                "flashsale.delay.exchange",
                "flashsale.cleanup",
                msg,
                message -> {
                    if (delayMillis >= 0L) {
                        message.getMessageProperties().setHeader("x-delay", delayMillis);
                    }
                    return message;
                }
        );

    }
}
