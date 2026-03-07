package edu.nchu.mall.services.flash_sale.listener;

import com.rabbitmq.client.Channel;
import edu.nchu.mall.models.to.mq.FlashSaleOrder;
import edu.nchu.mall.services.flash_sale.service.FlashSaleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Serializable;

@Slf4j
@Service
public class UserEventListener {

    @Autowired
    FlashSaleService flashSaleService;

    @RabbitListener(queues = "flashsale.event.queue")
    @RabbitHandler
    public void handleUserInfoChangedMessage(Serializable userId, Message message, Channel channel) {
        log.info("Received user info changed message: {}", message);

        if (userId instanceof Long) {
            try {
                flashSaleService.reCacheUserInfo((Long) userId).join();
            } catch (Exception e) {
                log.error("Failed to re-cache user info for userId {}: {}", userId, e.getMessage());
            }
        } else {
            log.warn("Received invalid user info changed message: {}", message);
        }
    }

    @RabbitListener(queues = "flashsale.cancel.queue")
    @RabbitHandler
    public void cancelFlashSaleOrder(FlashSaleOrder order, Message message, Channel channel) throws IOException {
        log.info("Received flash sale order cancel message: {}", message);

        try {
            flashSaleService.deleteUserPurchaseRecord(order.getUserId(), order.getSessionId(), order.getSkuId(), order.getRandomCode(), order.getNum());
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("Failed to cancel flash sale order for userId {}: {}", order.getUserId(), e.getMessage());
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}
