package edu.nchu.mall.services.third_party.service.impl;

import edu.nchu.mall.components.exception.CustomException;
import edu.nchu.mall.services.third_party.service.SimpleMailService;
import jakarta.annotation.Resource;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
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
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RefreshScope
public class ASimpleMailService implements SimpleMailService {

    public static final String CODE_KEY = "email:code:";
    public static final String LOCK_KEY = "email:lock:";
    public static final short KEEP_TIME_SECONDS = 300;
    public static final short RETRY_TIME_SECONDS = 60;

    @Resource
    JavaMailSender mailSender;

    @Autowired
    TemplateEngine templateEngine;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private void storeCode(@NotNull String to, @NotNull String code) {
        log.info("存储验证码：{}", code);
        redisTemplate.opsForValue().set(CODE_KEY + to, code, KEEP_TIME_SECONDS, TimeUnit.SECONDS);
    }

    @Async
    @Override
    public CompletableFuture<Boolean> send(@NotNull MailMessage mailMessage) {

        boolean isLocked = false, succeeded = true;
        RLock lock = redissonClient.getLock(LOCK_KEY + mailMessage.getTo());

        try {
            isLocked = lock.tryLock(3, 10, TimeUnit.SECONDS);
            if (!isLocked) {
                log.warn("获取邮件发送锁失败，系统正忙: {}", mailMessage.getTo());
                throw new CustomException("系统正忙");
            }

            if (!retryable(mailMessage)) {
                log.info("用户{}的请求过于频繁", mailMessage.getTo());
                throw new CustomException("请求过于频繁");
            }

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
            storeCode(mailMessage.getTo(), mailMessage.getCode());

            log.info("发送邮件成功");
        } catch (Exception e) {
            log.error("发送邮件失败: {}", e.toString());
            succeeded = false;
        } finally {
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

        return CompletableFuture.completedFuture(succeeded);
    }

    @Override
    public Boolean validate(Validation validation) {
        String code = redisTemplate.opsForValue().get(CODE_KEY + validation.getTarget());
        boolean res =  code != null && code.equals(validation.getCode());
        redisTemplate.delete(CODE_KEY + validation.getTarget());
        return res;
    }

    @Override
    public boolean retryable(MailMessage mailMessage) {
        Long expire = redisTemplate.getExpire(CODE_KEY + mailMessage.getTo(), TimeUnit.SECONDS);
        return expire == null || expire <= KEEP_TIME_SECONDS - RETRY_TIME_SECONDS;
    }

    @Override
    public boolean exists(MailMessage mailMessage) {
        return redisTemplate.opsForValue().get(CODE_KEY + mailMessage.getTo()) != null;
    }
}
