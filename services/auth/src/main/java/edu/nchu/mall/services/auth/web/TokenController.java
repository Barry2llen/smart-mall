package edu.nchu.mall.services.auth.web;

import edu.nchu.mall.services.auth.constants.RedisConstant;
import edu.nchu.mall.services.auth.utils.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Tag(name = "token管理")
@RefreshScope
@Controller
public class TokenController {

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Value(("${jwt.cookie.name.refresh}"))
    private String cookieName;

    @Parameters(@Parameter(name = "refreshToken", description = "刷新token"))
    @Operation(description = "刷新获取access token")
    @GetMapping("/refresh")
    @ResponseBody
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        // TODO 刷新token
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return ResponseEntity.status(401).body("token not exists");
        }

        Cookie refreshCookie = null;
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(cookieName)) {
                refreshCookie = cookie;
                break;
            }
        }

        if (refreshCookie == null) {
            return ResponseEntity.status(401).body("token not exists");
        }

        String refreshToken = refreshCookie.getValue();

        String exists = redisTemplate.opsForValue().get(RedisConstant.REFRESH_TOKEN_KEY + refreshToken);
        if (exists == null || exists.isBlank() || !exists.equals(refreshToken)) {
            return ResponseEntity.status(401).body("token expired");
        }

        if (!jwtUtils.validateToken(refreshToken)) {
            return ResponseEntity.status(401).body("invalid token");
        }

        String accessToken = jwtUtils.generateAccessToken(jwtUtils.getClaimsFromToken(refreshToken).getSubject(), null);

        return ResponseEntity.ok().header("Authorization", "Bearer " + accessToken).build();
    }
}
