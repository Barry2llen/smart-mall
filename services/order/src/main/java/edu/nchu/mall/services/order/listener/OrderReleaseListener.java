package edu.nchu.mall.services.order.listener;

import com.rabbitmq.client.Channel;
import edu.nchu.mall.models.entity.Order;
import edu.nchu.mall.services.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Slf4j
@Service
@RabbitListener(queues = "order.release.queue")
public class OrderReleaseListener {

    @Autowired
    OrderService orderService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @RabbitHandler
    @Transactional
    public void release(Order order, Channel channel, Message message) throws IOException {
        log.info("取消订单[sn={}]", order.getOrderSn());

        try {
            order = orderService.releaseOrder(order.getId());
            if (order != null) {
                // 发送消息通知库存解锁
                rabbitTemplate.convertAndSend("order.event.exchange", "order.stock.release", order);
            }
        } catch (Exception e) {
            log.error("取消订单失败", e);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            return;
        }

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
