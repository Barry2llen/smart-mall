package edu.nchu.mall.services.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.nchu.mall.models.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class OrderApplicationTests {

    @Autowired
    AmqpAdmin amqpAdmin;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    ObjectMapper mapper;

    @Test
    void createExchange() {
        // 1. 创建交换机
        amqpAdmin.declareExchange(new DirectExchange("test.direct"));
    }

    @Test
    void createQueue() {
        // 2. 创建队列
        amqpAdmin.declareQueue(new Queue("test.queue", true, false,  false));
    }

    @Test
    void bind() {
        // 3. 绑定
        amqpAdmin.declareBinding(new Binding("test.queue", Binding.DestinationType.QUEUE, "test.direct", "test", null));
    }

    @Test
    void sendMessage() throws JsonProcessingException {
        // 4. 发送消息
        User user = new User(1L, "admin", "123456");
        //String json = mapper.writeValueAsString(user);
        rabbitTemplate.convertAndSend("test.direct", "test", user);
    }
}
