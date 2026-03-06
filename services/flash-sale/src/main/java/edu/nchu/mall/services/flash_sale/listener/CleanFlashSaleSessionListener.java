package edu.nchu.mall.services.flash_sale.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import edu.nchu.mall.services.flash_sale.service.DelayMessageSender;
import edu.nchu.mall.services.flash_sale.service.FlashSaleService;
import edu.nchu.mall.services.flash_sale.constants.RedisConstant;
import edu.nchu.mall.services.flash_sale.rentity.FlashSaleCleanupMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Service
@RabbitListener(queues = "flashsale.delay.queue")
public class CleanFlashSaleSessionListener {

    @Autowired
    FlashSaleService flashSaleService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    DelayMessageSender delayMessageSender;

    @RabbitHandler
    public void handleCleanFlashSaleSessionMessage(FlashSaleCleanupMessage cleanupMessage, Channel channel, Message message) throws IOException {
        log.info("Received flash sale cleanup message: {}", cleanupMessage);
        try {
            if (cleanupMessage == null || cleanupMessage.getSessionId() == null) {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }

            String sessionIdStr = cleanupMessage.getSessionId().toString();
            Object cleanupMetaObj = redisTemplate.opsForHash().get(RedisConstant.FLASH_SALE_CLEANUP_META_KEY, sessionIdStr);
            if (cleanupMetaObj == null) {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }

            FlashSaleCleanupMessage latestCleanupMeta = mapper.readValue(cleanupMetaObj.toString(), FlashSaleCleanupMessage.class);
            if (!Objects.equals(cleanupMessage.getDigest(), latestCleanupMeta.getDigest())
                    || !Objects.equals(cleanupMessage.getCleanTime(), latestCleanupMeta.getCleanTime())) {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }

            if (latestCleanupMeta.getCleanTime() != null && LocalDateTime.now().isBefore(latestCleanupMeta.getCleanTime())) {
                delayMessageSender.sendDelayMessage(latestCleanupMeta, latestCleanupMeta.getCleanTime());
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }

            flashSaleService.cleanFlashSaleSessionCache(cleanupMessage.getSessionId().toString());
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);    // 处理失败，拒绝消息，不重新入队
        }
    }

}
