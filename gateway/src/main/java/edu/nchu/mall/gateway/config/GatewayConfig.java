package edu.nchu.mall.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class GatewayConfig {

    @PostConstruct
    public void initBlockHandler() {
        GatewayCallbackManager.setBlockHandler(urlBlockHandler());
    }

    public BlockRequestHandler urlBlockHandler() {
        return (exchange, e) -> {
            String msg = buildMessage(e);
            String path = exchange.getRequest().getURI().getPath();
            String result = String.format(
                    "{\"code\":429,\"msg\":\"%s\",\"data\":\"%s\"}",
                    msg,
                    path
            );

            return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(result));
        };
    }

    private String buildMessage(Throwable e) {
        if (!(e instanceof BlockException)) {
            return "请求被拦截，请稍后再试";
        }
        return switch (e) {
            case FlowException flowException -> "请求过于频繁，请稍后再试";
            case DegradeException degradeException -> "服务已降级，请稍后再试";
            case ParamFlowException paramFlowException -> "热点参数限流，请稍后再试";
            case AuthorityException authorityException -> "无访问权限";
            case SystemBlockException systemBlockException -> "系统保护规则触发，请稍后再试";
            default -> "请求被限流，请稍后再试";
        };
    }

}
