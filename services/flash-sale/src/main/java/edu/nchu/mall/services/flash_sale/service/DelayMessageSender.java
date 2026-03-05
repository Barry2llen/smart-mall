package edu.nchu.mall.services.flash_sale.service;

import edu.nchu.mall.services.flash_sale.rentity.FlashSaleCleanupMessage;

import java.time.LocalDateTime;

public interface DelayMessageSender {
    /**
     * 发送延迟消息
     *
     * @param message 消息内容
     * @param time 延迟到期时间
     */
    void sendDelayMessage(FlashSaleCleanupMessage message, LocalDateTime time);
}
