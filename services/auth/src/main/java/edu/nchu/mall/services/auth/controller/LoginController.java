package edu.nchu.mall.services.auth.controller;

import edu.nchu.mall.models.dto.UserLogin;
import edu.nchu.mall.models.dto.UserRegister;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.services.auth.service.LoginService;
import edu.nchu.mall.services.auth.utils.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "登录注册")
@RestController
@RefreshScope
public class LoginController {

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    LoginService loginService;

    @Value(("${jwt.cookie.name.refresh}"))
    private String cookieName;

    @GetMapping("/test")
    public String test(@RequestHeader("X-User-Id") String userId) {
        return userId;
    }

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
    public R<?> login(@RequestBody @Valid UserLogin body, HttpServletResponse response) {
        Long userId = loginService.login(body.getUsername(), body.getPassword());

        if (userId == null) return R.fail("账号或密码错误");

        String accessToken = jwtUtils.generateAccessToken(String.valueOf(userId), Map.of());
        String refreshToken = jwtUtils.generateRefreshToken(String.valueOf(userId));

        Cookie cookie = new Cookie(cookieName, refreshToken);
        cookie.setMaxAge((int)(jwtUtils.getRefreshExpirationMs() / 1000));
        cookie.setHttpOnly(true);
        cookie.setPath("/api/auth/token/refresh");

        response.addCookie(cookie);
        response.addHeader("Authorization", "Bearer " + accessToken);

        return R.success("登录成功");
    }

    @Operation(description = "用户登出")
    @GetMapping("/logout")
    public R<?> logout(@RequestHeader("X-User-Id") Long userId) {
        boolean res = loginService.logout(userId);
        return res ? R.success() : R.fail("登出失败");
    }
}
