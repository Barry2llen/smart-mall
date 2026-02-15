package edu.nchu.mall.services.third_party.service.impl;

import edu.nchu.mall.services.third_party.service.SimpleMailService;
import jakarta.annotation.Resource;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RefreshScope
public class ASimpleMailService implements SimpleMailService {

    public static final String CODE_KEY = "email:code:";

    @Resource
    JavaMailSender mailSender;

    @Autowired
    TemplateEngine templateEngine;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private CompletableFuture<Void> storeCode(@NotNull String to, @NotNull String code) {
        return CompletableFuture.runAsync(() -> {
            redisTemplate.opsForValue().set(CODE_KEY + to, code, 300, TimeUnit.SECONDS);
        });
    }

    @Async
    @Override
    public CompletableFuture<Void> send(@NotNull MailMessage mailMessage) {

        storeCode(mailMessage.getTo(), mailMessage.getCode());

        try {
            // 1. 创建 Thymeleaf 上下文对象，用于存放变量
            Context context = new Context();
            context.setVariable("username", mailMessage.getUsername());
            context.setVariable("code", mailMessage.getCode());

            // 2. 渲染模板：指定模板名称和上下文
            // process 会寻找 templates 目录下的 email.html
            String emailContent = templateEngine.process("email", context);

            // 3. 构建 MimeMessage 发送邮件
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(mailMessage.getTo());
            helper.setSubject(mailMessage.getSubject());
            helper.setText(emailContent, true); // 这里的 true 必须带上，表示是 HTML

            mailSender.send(message);

            log.info("发送邮件成功");
        } catch (Exception e) {
            log.error("发送邮件失败");
        }

        return CompletableFuture.completedFuture(null);
    }

    @Override
    public Boolean validate(Validation validation) {
        String code = redisTemplate.opsForValue().get(CODE_KEY + validation.getTarget());
        return code != null && code.equals(validation.getCode());
    }
}
