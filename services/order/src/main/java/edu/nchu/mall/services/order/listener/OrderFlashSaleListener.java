package edu.nchu.mall.services.order.listener;

import com.rabbitmq.client.Channel;
import edu.nchu.mall.models.to.mq.FlashSaleOrder;
import edu.nchu.mall.services.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RabbitListener(queues = "order.flashsale.queue")
public class OrderFlashSaleListener {

    @Autowired
    OrderService orderService;

    @RabbitHandler
    public void handleFlashSaleOrderMessage(FlashSaleOrder order, Channel channel, Message message) {
        log.info("Received flash sale order message: {}", order);
    }

}
