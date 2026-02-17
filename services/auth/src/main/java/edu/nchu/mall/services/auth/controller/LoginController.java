package edu.nchu.mall.services.auth.controller;

import edu.nchu.mall.models.dto.UserLogin;
import edu.nchu.mall.models.dto.UserRegister;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.services.auth.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "登录注册")
@RestController
public class LoginController {
    @Autowired
    LoginService loginService;

    @Parameters(@Parameter(name = "email", description = "用户邮箱"))
    @Operation(description = "发送验证码")
    @PostMapping("/sendCode")
    public R<?> sendCode(@RequestBody @Valid String email) {
        boolean res = loginService.sendCode(email);
        return res ? R.success() : R.fail("发送验证码失败");
    }

    @Parameters({
            @Parameter(name = "body", description = "用户注册信息")
    })
    @Operation(description = "用户注册")
    @PostMapping("/register")
    public R<?> register(@RequestBody @Valid UserRegister body) {
        boolean res = loginService.register(body.getUsername(), body.getPassword(), body.getEmail(), body.getCode());
        return res ? R.success() : R.fail("注册失败");
    }

    @Parameters({
            @Parameter(name = "body", description = "用户登录信息")
    })
    @Operation(description = "用户登录")
    @PostMapping("/login")
    public R<?> login(@RequestBody @Valid UserLogin body) {
        boolean res = loginService.login(body.getUsername(), body.getPassword());
        return res ? R.success() : R.fail("账号或密码错误");
    }
}
