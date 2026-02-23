package edu.nchu.mall.components.resolver;

import edu.nchu.mall.models.annotation.bind.UserId;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class UserIdResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(UserId.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String userId = webRequest.getHeader("X-User-Id");

        if (userId == null || userId.isEmpty()) {
            if (parameter.getParameterAnnotation(UserId.class).required()) {
                throw new IllegalArgumentException("用户ID不能为空");
            }
            return null;
        }

        if (parameter.getParameterType().equals(Long.class)) {
            return Long.parseLong(userId);
        }
        if (parameter.getParameterType().equals(String.class)) {
            return userId;
        }

        throw new RuntimeException("Invalid Type");
    }
}
