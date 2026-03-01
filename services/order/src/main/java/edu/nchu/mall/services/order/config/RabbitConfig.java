package edu.nchu.mall.services.order.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

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

    @Bean
    public Queue orderDelayQueue() {
        return new Queue("order.delay.queue", true, false, false,
                Map.of(
                        "x-dead-letter-exchange", "order.event.exchange",
                        "x-dead-letter-routing-key", "order.release",
                        "x-message-ttl", 60000
                )
        );
    }

    @Bean
    public Queue orderReleaseQueue() {
        return new Queue("order.release.queue", true, false, false);
    }

    @Bean
    public Exchange orderEventExchange() {
        return new TopicExchange("order.event.exchange", true, false);
    }

    @Bean
    public Binding orderDelayBinding() {
        return new Binding("order.delay.queue", Binding.DestinationType.QUEUE, "order.event.exchange", "order.create.#", null);
    }

    @Bean
    public Binding orderReleaseBinding() {
        return new Binding("order.release.queue", Binding.DestinationType.QUEUE, "order.event.exchange", "order.release.#", null);
    }

    @Bean
    public Binding orderStockReleaseBinding() {
        return new Binding("stock.release.queue", Binding.DestinationType.QUEUE, "order.event.exchange", "order.stock.release.#", null);
    }
}
