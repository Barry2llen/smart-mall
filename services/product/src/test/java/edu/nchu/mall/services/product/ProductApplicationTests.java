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

    private User getUser(){
        return userService.users().get(0);
    }

    @Test
    void insertUser() {
        User user = new User();
        user.setUsername("barry");
        user.setPassword("123456");
        userService.saveUser(user);
    }

    @Test
    void testRedis() throws JsonProcessingException {
        User user = getUser();
        System.out.println("MySQL:" + user);
        stringRedisTemplate.opsForValue().set("user:" + user.getId(), objectMapper.writeValueAsString(user));

        String json = stringRedisTemplate.opsForValue().get("user:" + user.getId());
        User user2 = objectMapper.readValue(json, User.class);
        System.out.println("Redis:" + user2);
    }
}
