package edu.nchu.mall.services.auth.handler;

import edu.nchu.mall.components.exception.CustomException;
import edu.nchu.mall.components.feign.member.MemberFeignClient;
import edu.nchu.mall.models.entity.Member;
import edu.nchu.mall.services.auth.constants.RedisConstant;
import edu.nchu.mall.services.auth.utils.JwtUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@RefreshScope
@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    MemberFeignClient memberFeignClient;

    @Autowired
    StringRedisTemplate redisTemplate;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value(("${jwt.cookie.name.refresh}"))
    private String cookieName;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {

        // 1. 获取 GitHub 登录成功后的用户信息
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String loginName = oAuth2User.getAttribute("login");
        String email = oAuth2User.getAttribute("email");

        if (Objects.isNull(email)) {
            email = fetchPrimaryEmail(oauthToken);
        }

        if (email == null || email.isBlank()) {
            throw new CustomException("无法获取邮箱", null, HttpStatus.BAD_REQUEST);
        }

        Member member = null;
        try {
            member = memberFeignClient.putByEmail(email, loginName);
        } catch (Exception e) {
            throw new CustomException("无法创建或登录用户账号", null, HttpStatus.BAD_REQUEST);
        }
        if (member == null) {
            throw new CustomException("无法创建或登录用户账号", null, HttpStatus.BAD_REQUEST);
        }

        String accessToken = jwtUtils.generateAccessToken(String.valueOf(member.getId()), Map.of());
        String refreshToken = jwtUtils.generateRefreshToken(String.valueOf(member.getId()));

        Cookie cookie = new Cookie(cookieName, refreshToken);
        cookie.setMaxAge((int)(jwtUtils.getRefreshExpirationMs() / 1000));
        cookie.setHttpOnly(true);
        cookie.setPath("/api/auth/refresh");

        Long id = member.getId();
        CompletableFuture<Boolean> t = CompletableFuture.supplyAsync(() -> {
            try {
                redisTemplate.opsForValue().set(
                        RedisConstant.REFRESH_TOKEN_KEY + refreshToken,
                        String.valueOf(id),
                        jwtUtils.getRefreshExpirationMs(),
                        TimeUnit.MILLISECONDS
                );
                return true;
            } catch (Exception e) {
                return false;
            }
        });

        CompletableFuture<Boolean> i = CompletableFuture.supplyAsync(() -> {
            try {
                redisTemplate.opsForValue().set(
                        RedisConstant.REFRESH_TOKEN_KEY + id,
                        refreshToken,
                        jwtUtils.getRefreshExpirationMs(),
                        TimeUnit.MILLISECONDS
                );
                return true;
            } catch (Exception e) {
                return false;
            }
        });

        CompletableFuture.allOf(t, i).join();
        try {
            if (!t.isDone() || !i.isDone() || t.get().equals(Boolean.FALSE) || i.get().equals(Boolean.FALSE)) {
                throw new CustomException("无法连接redis", null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        response.addCookie(cookie);
        response.addHeader("Authorization", "Bearer " + accessToken);

    }

    /**
     * 手动请求 GitHub /user/emails 接口
     */
    private String fetchPrimaryEmail(OAuth2AuthenticationToken oauthToken) {
        // 获取当前客户端的授权信息
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(),
                oauthToken.getName());

        String accessToken = client.getAccessToken().getTokenValue();

        // 构建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>("", headers);

        try {
            // 请求 GitHub 获取邮箱列表
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    "https://api.github.com/user/emails",
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );

            if (response.getBody() != null) {
                // 筛选出 primary 为 true 的邮箱
                return response.getBody().stream()
                        .filter(m -> (boolean) m.get("primary"))
                        .map(m -> (String) m.get("email"))
                        .findFirst()
                        .orElse(null);
            }
        } catch (Exception e) {
            logger.error("获取 GitHub Email 失败", e);
        }
        return null;
    }
}