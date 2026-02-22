package edu.nchu.mall.gateway.filter;

import io.jsonwebtoken.Claims;
import edu.nchu.mall.gateway.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class JwtAuthGlobalFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtUtils jwtUtils;

    private final static Set<String> WHITE_LIST;
    private final static Map<String, String> WHITE_LIST_HEADERS;
    private final static Set<String> HEADERS;

    static {
        WHITE_LIST = Set.of(
                "/auth/login",
                "/auth/register",
                "/auth/refresh"
        );

        HEADERS = Set.of(
                "X-User-Id"
        );

        WHITE_LIST_HEADERS = Map.of(
                "X-PWD", "barry2llen"
        );
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        for (Map.Entry<String, List<String>> entry : request.getHeaders().entrySet()) {
            if (WHITE_LIST_HEADERS.containsKey(entry.getKey())) {
                if (WHITE_LIST_HEADERS.get(entry.getKey()).equals(entry.getValue().getFirst())) {
                    return chain.filter(exchange);
                }
            }

            if (HEADERS.contains(entry.getKey())) {
                return unauthorizedResponse(exchange, "非法请求");
            }
        }

        // 1. 白名单放行：登录、OAuth2 回调、注册等接口不需要 Token
        if (WHITE_LIST.contains(path) || path.contains("oauth2")) {
            return chain.filter(exchange);
        }

        // 2. 提取并校验 Token
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            return unauthorizedResponse(exchange, "缺少 Token");
        }

        String token = authHeader.substring(7);

        try {
            // 3. 验证 Token 有效性
            if (!jwtUtils.validateToken(token)) {
                return unauthorizedResponse(exchange, "Token 无效或已过期");
            }

            // 4. 解析 Token 获取用户信息 (例如 userId)
            Claims claims = jwtUtils.getClaimsFromToken(token);
            String userId = claims.getSubject();

            // 5. 【核心步骤】请求头透传 (Mutate Request)
            // WebFlux 中的 Request 是不可变的，必须使用 mutate() 方法克隆并修改
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Id", userId) // 把解析出的 userId 塞进请求头
                    // .header("X-User-Name", claims.get("username", String.class)) // 如果需要也可以传名字
                    .build();

            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(mutatedRequest)
                    .build();

            // 6. 放行，带着带有 userId 的新请求头去请求下游微服务
            return chain.filter(mutatedExchange);

        } catch (Exception e) {
            return unauthorizedResponse(exchange, "Token 解析失败");
        }
    }

    /**
     * 封装 401 拦截响应
     */
    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String msg) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");

        // 简单返回一个 JSON 错误提示
        String result = String.format("{\"code\": 401, \"message\": \"%s\"}", msg);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(result.getBytes())));
    }

    /**
     * 设置过滤器的优先级，数字越小优先级越高。
     * 我们希望它在路由转发之前执行，所以设为 -100。
     */
    @Override
    public int getOrder() {
        return -100;
    }
}