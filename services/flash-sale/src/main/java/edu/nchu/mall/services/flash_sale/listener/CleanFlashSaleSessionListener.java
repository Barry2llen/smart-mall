package edu.nchu.mall.services.flash_sale.listener;

import com.rabbitmq.client.Channel;
import edu.nchu.mall.services.flash_sale.service.FlashSaleService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RabbitListener(queues = "flashsale.delay.queue")
public class CleanFlashSaleSessionListener {

    @Autowired
    FlashSaleService flashSaleService;

    @RabbitHandler
    public void handleCleanFlashSaleSessionMessage(Long sessionId, Channel channel, Message message) throws IOException {
        try {
            flashSaleService.cleanFlashSaleSessionCache(sessionId);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);    // 处理失败，拒绝消息，不重新入队
        }
    }

}
