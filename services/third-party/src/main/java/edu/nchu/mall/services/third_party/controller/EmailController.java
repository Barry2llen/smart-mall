package edu.nchu.mall.services.third_party.controller;

import edu.nchu.mall.models.model.R;
import edu.nchu.mall.services.third_party.service.SimpleMailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ThreadLocalRandom;

@Tag(name = "邮件验证服务")
@RestController
@RequestMapping("email")
public class EmailController {

    @Autowired
    SimpleMailService mailService;

    @Parameters({
            @Parameter(name = "username", description = "用户名", required = true),
            @Parameter(name = "email", description = "邮箱", required = true),
            @Parameter(name = "subject", description = "主题", required = true)
    })
    @Operation(description = "向邮箱发送验证码")
    @GetMapping("/send")
    public R<?> sendCode(@RequestParam @NotBlank String username,
                         @RequestParam @NotBlank String email,
                         @RequestParam @NotBlank String subject
    ) {
        String code = ThreadLocalRandom.current().nextInt(100000, 999999) + "";
        mailService.send(new SimpleMailService.MailMessage(username, email, subject, code));
        return R.success(null);
    }

    @Parameters({
            @Parameter(name = "email", description = "邮箱", required = true),
            @Parameter(name = "code", description = "验证码", required = true)
    })
    @Operation(description = "验证邮箱验证码")
    @GetMapping("/verify")
    public R<Boolean> verifyCode(@RequestParam @NotBlank String code, @RequestParam @NotBlank String email) {
        return R.success(mailService.validate(new SimpleMailService.Validation(email, code)));
    }
}
