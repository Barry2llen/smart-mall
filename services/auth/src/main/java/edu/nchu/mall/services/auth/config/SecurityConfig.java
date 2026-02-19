package edu.nchu.mall.services.auth.config;

import edu.nchu.mall.services.auth.handler.OAuth2LoginSuccessHandler;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 针对前后端分离项目，通常禁用 CSRF
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                // 配置会话管理为无状态，因为我们要用 JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 将静态资源加入白名单
                        .requestMatchers("/**").permitAll()
                        .anyRequest().authenticated()
                )
                // 处理未授权请求，返回 JSON 而不是跳转 HTML
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        })
                )
                .oauth2Login(oauth2 -> oauth2
                        // 核心配置：指定成功后的处理器
                        .successHandler(oAuth2LoginSuccessHandler)
                );

        return http.build();
    }
}