package edu.nchu.mall.services.order.listener;

import com.rabbitmq.client.Channel;
import edu.nchu.mall.models.enums.Payment;
import edu.nchu.mall.models.to.mq.FlashSaleOrder;
import edu.nchu.mall.services.order.dto.FlashSaleOrderSubmit;
import edu.nchu.mall.services.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RabbitListener(queues = "order.flashsale.queue")
public class OrderFlashSaleListener {

    @Autowired
    OrderService orderService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @RabbitHandler
    public void handleFlashSaleOrderMessage(FlashSaleOrder order, Channel channel, Message message) throws IOException {
        log.info("Received flash sale order message: {}", order);
        log.info("正在处理秒杀订单，订单号：{}", order.getOrderSn());

        try {
            FlashSaleOrderSubmit orderSubmit = new FlashSaleOrderSubmit();
            BeanUtils.copyProperties(order, orderSubmit);
            orderSubmit.setPayment(Payment.ALIPAY); // 假设秒杀订单默认使用支付宝支付
            orderSubmit.setAddrId(order.getAddressId());
            orderSubmit.setNotes(order.getNote());

            OrderService.OrderSubmitStatus status = orderService.submitFlashSaleOrder(order.getUserId(), orderSubmit, order);

            if (status != OrderService.OrderSubmitStatus.OK) {
                log.warn("Flash sale order submission failed for orderSn {}: {}", order.getOrderSn(), status.getMessage());
                rabbitTemplate.convertAndSend("flashsale.event.exchange", "flashsale.cancel", order);
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("Error processing flash sale order: {}", e.getMessage(), e);
            rabbitTemplate.convertAndSend("flashsale.event.exchange", "flashsale.cancel", order);
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
        }
    }

}
