package edu.nchu.mall.services.auth;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AuthApplicationTests {

    @Autowired
    HttpSession session;

    @Test
    void contextLoads() {
        System.out.println(session.getClass());
    }
}
