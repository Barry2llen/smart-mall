package edu.nchu.mall.services.order.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RabbitConfig {

    @Bean
    RabbitTemplateCustomizer rabbitTemplateCustomizer() {
        return rabbitTemplate -> {
            rabbitTemplate.setConfirmCallback((data, ack, cause) -> {
                if (ack) {
                    log.info("消息发送成功");
                } else {
                    log.error("消息发送失败: {}", cause);
                }
            });
            rabbitTemplate.setReturnsCallback(msg -> {
                log.error("消息进入队列失败：{}", msg);
            });
        };
    }

    @Bean
    MessageConverter messageConverter(ObjectMapper mapper) {
        return new Jackson2JsonMessageConverter(mapper);
    }
}
