package edu.nchu.mall.services.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.nchu.mall.models.entity.User;
import edu.nchu.mall.services.product.service.UserService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
public class ProductApplicationTests {

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    UserService userService;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void contextLoads() {
    }


}
