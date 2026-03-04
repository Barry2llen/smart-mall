package edu.nchu.mall.services.flash_sale.service;

import java.time.LocalDateTime;

public interface DelayMessageSender {
    /**
     * 发送延迟消息
     *
     * @param message 消息内容
     * @param time 延迟到期时间
     */
    void sendDelayMessage(Object message, LocalDateTime time);
}
