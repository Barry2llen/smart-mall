package com.ruoyi.framework.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import com.ruoyi.framework.config.properties.PermitAllUrlProperties;
import com.ruoyi.framework.security.filter.JwtAuthenticationTokenFilter;
import com.ruoyi.framework.security.handle.AuthenticationEntryPointImpl;
import com.ruoyi.framework.security.handle.LogoutSuccessHandlerImpl;

/**
 * spring security配置（网关统一处理跨域，这里不再注入 CorsFilter）
 */
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Configuration
public class SecurityConfig
{
    /** 自定义用户验证逻辑 */
    @Autowired
    private UserDetailsService userDetailsService;

    /** 认证失败处理 */
    @Autowired
    private AuthenticationEntryPointImpl unauthorizedHandler;

    /** 退出处理 */
    @Autowired
    private LogoutSuccessHandlerImpl logoutSuccessHandler;

    /** token 认证过滤器 */
    @Autowired
    private JwtAuthenticationTokenFilter authenticationTokenFilter;

    /** 允许匿名访问的地址 */
    @Autowired
    private PermitAllUrlProperties permitAllUrl;

    /** 认证管理器 */
    @Bean
    public AuthenticationManager authenticationManager()
    {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return new ProviderManager(daoAuthenticationProvider);
    }

    /**
     * 安全过滤链
     */
    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception
    {
        return httpSecurity
            .cors(Customizer.withDefaults())
            // CSRF 关闭（无 session）
            .csrf(csrf -> csrf.disable())
            // 关闭默认缓存与 frame 限制
            .headers(headers -> headers.cacheControl(cache -> cache.disable()).frameOptions(options -> options.sameOrigin()))
            // 认证失败处理
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
            // 基于 token，无需 session
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 放行白名单与静态资源
            .authorizeHttpRequests(requests -> {
                permitAllUrl.getUrls().forEach(url -> requests.requestMatchers(url).permitAll());
                requests.requestMatchers("/login", "/register", "/captchaImage").permitAll()
                    .requestMatchers(HttpMethod.GET, "/", "/*.html", "/profile/**").permitAll()
                    .requestMatchers(
                        new AntPathRequestMatcher("/**/*.html"),
                        new AntPathRequestMatcher("/**/*.css"),
                        new AntPathRequestMatcher("/**/*.js"))
                    .permitAll()
                    .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/v3/api-docs.yaml", "/druid/**").permitAll()
                    .anyRequest().authenticated();
            })
            // 登出处理
            .logout(logout -> logout.logoutUrl("/logout").logoutSuccessHandler(logoutSuccessHandler))
            // JWT filter
            .addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    /** 密码加密器 */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder()
    {
        return new BCryptPasswordEncoder();
    }
}
