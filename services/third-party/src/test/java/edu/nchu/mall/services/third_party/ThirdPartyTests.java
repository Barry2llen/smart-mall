package edu.nchu.mall.services.third_party;

import edu.nchu.mall.services.third_party.service.SimpleMailService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@Slf4j
@SpringBootTest
public class ThirdPartyTests {

    @Autowired
    SimpleMailService mailService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Test
    void contextLoads() {
        mailService.send(new SimpleMailService.MailMessage("用户", "barry2llen4g@gmail.com", "Test", "123456")).join();
    }

    @Test
    void testRedisTtl() {
        Long expire = redisTemplate.getExpire("123");
        log.info("expire: {}", expire);
    }
}
