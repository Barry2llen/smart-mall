package edu.nchu.mall.services.product.advice;

import edu.nchu.mall.services.product.model.R;
import edu.nchu.mall.services.product.model.RCT;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@Hidden
@RestControllerAdvice
@Slf4j
public class GlobalControllerAdvice {

    public static record UnknowExceptionResponseMessage(Class<?> exceptionClass, String message) {}

    /**
     * 未知异常处理
     */
    @ExceptionHandler(Throwable.class)
    public R<?> exception(Throwable throwable) {
        log.error("未知异常：" + throwable.getMessage(), throwable);
        return new R<>(RCT.UNKNOWN_EXCEPTION, "未知异常", new UnknowExceptionResponseMessage(throwable.getClass(), throwable.getMessage()));
    }

    /**
     * 参数校验异常处理
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<?> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        Map<String,String> errorMap = bindingResult.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, fieldError -> fieldError.getDefaultMessage() == null ? "" : fieldError.getDefaultMessage()));
        return new R<>(RCT.VALIDATION_FAILED, "参数校验失败", errorMap);
    }
}
