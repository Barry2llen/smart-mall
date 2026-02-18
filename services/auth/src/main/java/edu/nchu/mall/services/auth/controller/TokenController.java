package edu.nchu.mall.services.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Tag(name = "token管理")
@RequestMapping("token")
@RefreshScope
public class TokenController {

    @Value(("${jwt.cookie.name.refresh}"))
    private String cookieName;

    @Parameters(@Parameter(name = "refreshToken", description = "刷新token"))
    @Operation(description = "刷新获取access token")
    @RequestMapping("/refresh")
    @ResponseBody
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        // TODO 刷新token
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

        return ResponseEntity.ok("success");
    }
}
