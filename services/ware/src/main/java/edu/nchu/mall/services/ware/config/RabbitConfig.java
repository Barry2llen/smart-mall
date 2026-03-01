package edu.nchu.mall.services.ware.config;

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
    public Exchange stockEventExchange() {
        return new TopicExchange("stock.event.exchange", true, false);
    }

    @Bean
    public Queue stockReleaseQueue() {
        return new Queue("stock.release.queue", true, false, false);
    }

    @Bean
    public Queue stockDelayQueue() {
        return new Queue("stock.delay.queue", true, false, false,
                Map.of(
                        "x-dead-letter-exchange", "stock.event.exchange",
                        "x-dead-letter-routing-key", "stock.release",
                        "x-message-ttl", 90000
                )
        );
    }

    @Bean
    public Binding stockReleaseBinding() {
        return new Binding("stock.release.queue", Binding.DestinationType.QUEUE, "stock.event.exchange", "stock.release.#", null);
    }

    @Bean
    public Binding stockDelayBinding() {
        return new Binding("stock.delay.queue", Binding.DestinationType.QUEUE, "stock.event.exchange", "stock.delay.#", null);
    }
}
