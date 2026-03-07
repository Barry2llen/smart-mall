package edu.nchu.mall.services.member.notice.event.impl;

import edu.nchu.mall.models.dto.MemberReceiveAddressDTO;
import edu.nchu.mall.models.notice.event.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Slf4j
@Component
public class FlashSaleUserInfoChanged implements Event {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    public void handle(Object... args) {

        if (args == null || args.length == 0) {
            log.warn("No arguments provided for FlashSaleUserInfoChanged event");
            return;
        }

        Object obj = args[0];
        switch (obj) {
            case MemberReceiveAddressDTO dto -> {
                sendUserInfoChangedMessage(dto.getMemberId());
            }
            case Serializable userId -> {
                sendUserInfoChangedMessage(userId);
            }
            default -> throw new IllegalStateException("Unexpected value: " + obj);
        }
    }

    private void sendUserInfoChangedMessage(Serializable userId) {
        // 发送消息到RabbitMQ，通知其他服务用户信息已变更
        rabbitTemplate.convertAndSend("flashsale.event.exchange", "flashsale.event.userinfochanged", userId);
        log.info("Sent user info changed message for userId: {}", userId);
    }
}
