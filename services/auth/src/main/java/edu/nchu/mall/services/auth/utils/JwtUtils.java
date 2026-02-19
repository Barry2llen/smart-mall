package edu.nchu.mall.services.auth.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RefreshScope
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Getter
    @Value("${jwt.expiration-ms.refresh}")
    private long refreshExpirationMs;

    @Getter
    @Value("${jwt.expiration-ms.access}")
    private long accessExpirationMs;

    /**
     * 生成 Access Token
     * @param subject 主题
     * @param claims 其他信息
     * @return JWT Token
     */
    public String generateAccessToken(String subject, Map<String, Object> claims) {
        Map<String, Object> _claims = new HashMap<>(claims == null ? Map.of() : claims);
        _claims.put("type", "access");
        return generateToken(subject, _claims, accessExpirationMs);
    }


    /**
     * 生成 Refresh Token
     * @param subject 主题
     * @return JWT Token
     */
    public String generateRefreshToken(String subject) {
        return generateToken(subject, Map.of("type", "refresh"), refreshExpirationMs);
    }

    private String generateToken(String subject, Map<String, Object> claims, long expirationMs) {
        Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));

        JwtBuilder builder = Jwts.builder();
        for (Map.Entry<String, Object> entry : claims.entrySet()) {
            builder.claim(entry.getKey(), entry.getValue());
        }

        return builder
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    /**
     * 解析并验证 Token
     */
    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 验证 Token 是否有效
     */
    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // 这里可以记录日志：Token 已过期、签名错误或格式不对
            return false;
        }
    }
}
