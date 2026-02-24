package edu.nchu.mall.gateway.filter;

import io.jsonwebtoken.Claims;
import edu.nchu.mall.gateway.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
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

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;

//@Slf4j
@Component
public class JwtAuthGlobalFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtUtils jwtUtils;

    private final static Set<String> WHITE_LIST;
    private final static Map<String, String> WHITE_LIST_HEADERS;
//    private final static Set<String> HEADERS;

    static {
        WHITE_LIST = Set.of(
                "/auth/public/login",
                "/auth/public/register",
                "/auth/public/refresh",
                "/auth/public/sendCode"
        );

//        HEADERS = Set.of(
//                "X-User-Id"
//        );

        WHITE_LIST_HEADERS = Map.of(
                "X-PWD", "barry2llen"
        );
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 1. 白名单放行：登录、OAuth2 回调、注册等接口不需要 Token
        if (WHITE_LIST.contains(path) || path.contains("oauth2")) {
            return chain.filter(exchange);
        }

        // 非public api不验证token
        if (!path.contains("public")) {
//            String clientIp = resolveClientIp(request);
//            if (!isInternalIp(clientIp)) {
//                log.error("非内网访问");
//                return notFoundResponse(exchange, "not found");
//            }

            for (Map.Entry<String, String> entry : WHITE_LIST_HEADERS.entrySet()) {
                if (request.getHeaders().containsKey(entry.getKey()) && request.getHeaders().get(entry.getKey()).get(0).equals(entry.getValue())) {
                    return chain.filter(exchange);
                }
            }
//            log.error("内网访问但未携带密钥");
            return notFoundResponse(exchange, "not found");
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
     * 封装 404 拦截响应
     */
    private Mono<Void> notFoundResponse(ServerWebExchange exchange, String msg) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.NOT_FOUND);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");

        // 简单返回一个 JSON 错误提示
        String result = String.format("{\"code\": 404, \"message\": \"%s\"}", msg);
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

    private String resolveClientIp(ServerHttpRequest request) {
        String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (StringUtils.hasText(xForwardedFor)) {
            String[] ips = xForwardedFor.split(",");
            for (String ip : ips) {
                String normalized = normalizeIp(ip);
                if (StringUtils.hasText(normalized)) {
                    return normalized;
                }
            }
        }

        String xRealIp = normalizeIp(request.getHeaders().getFirst("X-Real-IP"));
        if (StringUtils.hasText(xRealIp)) {
            return xRealIp;
        }

        InetSocketAddress remoteAddress = request.getRemoteAddress();
        if (remoteAddress != null && remoteAddress.getAddress() != null) {
            return normalizeIp(remoteAddress.getAddress().getHostAddress());
        }
        return null;
    }

    private String normalizeIp(String ip) {
        if (!StringUtils.hasText(ip)) {
            return null;
        }
        String normalized = ip.trim();
        if (!StringUtils.hasText(normalized) || "unknown".equalsIgnoreCase(normalized)) {
            return null;
        }
        int colonIndex = normalized.indexOf(':');
        if (colonIndex > 0 && normalized.indexOf('.') > -1) {
            normalized = normalized.substring(0, colonIndex);
        }
        return normalized;
    }

    private boolean isInternalIp(String ip) {
        if (!StringUtils.hasText(ip) || ip.contains(":")) {
            return false;
        }

        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            return false;
        }

        int[] numbers = new int[4];
        for (int i = 0; i < 4; i++) {
            try {
                numbers[i] = Integer.parseInt(parts[i]);
            } catch (NumberFormatException e) {
                return false;
            }
            if (numbers[i] < 0 || numbers[i] > 255) {
                return false;
            }
        }

        int first = numbers[0];
        int second = numbers[1];

        if (first == 10 || first == 127) {
            return true;
        }
        if (first == 192 && second == 168) {
            return true;
        }
        return first == 172 && second >= 16 && second <= 31;
    }
}
