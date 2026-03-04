package edu.nchu.mall.services.flash_sale.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class RabbitConfig {

    @Bean
    MessageConverter messageConverter(ObjectMapper mapper) {
        return new Jackson2JsonMessageConverter(mapper);
    }

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

    // 1. 声明自定义类型的延迟交换机
    @Bean
    public CustomExchange delayedExchange() {
        Map<String, Object> args = new HashMap<>();
        // 核心参数：告诉插件这个交换机在延迟到期后按照什么逻辑分发消息
        args.put("x-delayed-type", "direct");

        return new CustomExchange(
                "flashsale.delay.exchange",
                "x-delayed-message", // 固定类型：插件识别的标识
                true,                // 是否持久化
                false,               // 是否自动删除
                args                 // 扩展参数
        );
    }

    // 2. 声明普通队列
    @Bean
    public Queue delayedQueue() {
        return new Queue("flashsale.delay.queue", true);
    }

    // 3. 将队列绑定到延迟交换机
    @Bean
    public Binding bindingDelayed(Queue delayedQueue, CustomExchange delayedExchange) {
        return BindingBuilder.bind(delayedQueue)
                .to(delayedExchange)
                .with("flashsale.cleanup.#")
                .noargs();
    }
}
