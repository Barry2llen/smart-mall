package edu.nchu.mall.components.advice;

import edu.nchu.mall.components.exception.CustomException;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.core.MethodParameter;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Hidden
@RestControllerAdvice
@Slf4j
public class GlobalControllerAdvice{

    @Value("${spring.application.name:unknown}")
    String service;

    public record UnknowExceptionResponseMessage(Class<?> exceptionClass, String message) {}

    /**
     * 未知异常处理
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<R<?>> exception(Throwable throwable) {
        log.error("未知异常：{}", throwable.getMessage(), throwable);
        return new ResponseEntity<>(new R<>(RCT.UNKNOWN_EXCEPTION, "未知异常", new UnknowExceptionResponseMessage(throwable.getClass(), throwable.getMessage())), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 参数不合法
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<R<?>> illegalArgumentException(IllegalArgumentException e){
        return new ResponseEntity<>(new R<>(RCT.VALIDATION_FAILED, "参数不合法：" + e.getMessage(), null), HttpStatus.BAD_REQUEST);
    }

    /**
     * 参数校验异常处理
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<R<?>> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        Map<String,String> errorMap = bindingResult.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, fieldError -> fieldError.getDefaultMessage() == null ? "" : fieldError.getDefaultMessage()));
        return new ResponseEntity<>(new R<>(RCT.VALIDATION_FAILED, "参数校验失败", errorMap), HttpStatus.BAD_REQUEST);
    }

    /**
     * 参数校验异常处理
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<R<?>> handlerMethodValidationException(HandlerMethodValidationException e){
        Map<String, String> errorMap = new LinkedHashMap<>();
        e.getAllValidationResults().forEach(result -> {
            MethodParameter methodParameter = result.getMethodParameter();
            String parameterName = methodParameter.getParameterName();
            if (parameterName == null || parameterName.isBlank()) {
                parameterName = "arg" + methodParameter.getParameterIndex();
            }

            String message = result.getResolvableErrors().stream()
                    .map(MessageSourceResolvable::getDefaultMessage)
                    .filter(Objects::nonNull)
                    .filter(msg -> !msg.isBlank())
                    .distinct()
                    .collect(Collectors.joining("; "));
            if (!message.isBlank()) {
                errorMap.put(parameterName, message);
            }
        });

        String responseMessage = errorMap.isEmpty()
                ? (e.getReason() == null || e.getReason().isBlank() ? "参数校验失败" : e.getReason())
                : "参数校验失败";

        return new ResponseEntity<>(new R<>(RCT.VALIDATION_FAILED, responseMessage, errorMap.isEmpty() ? null : errorMap), HttpStatus.BAD_REQUEST);
    }

    /**
     * 缺少参数
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<R<?>> missingServletRequestParameterException(MissingServletRequestParameterException e){
        return new ResponseEntity<>(new R<>(RCT.VALIDATION_FAILED, "缺少参数：" + e.getParameterName(), null), HttpStatus.BAD_REQUEST);
    }

    /**
     * 路径错误
     */
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class, NoResourceFoundException.class})
    public ResponseEntity<?> noResourceFoundException(Exception e){
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * 非法id：Long.valueOf转换时出错
     */
    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<R<?>> numberFormatException(NumberFormatException e){
        return new ResponseEntity<>(R.fail("id错误"), HttpStatus.BAD_REQUEST);
    }

    /**
     * json格式错误
     */
    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<?> httpMessageNotReadableException(HttpMessageNotReadableException e){
        return new ResponseEntity<>(R.fail("json格式错误"), HttpStatus.BAD_REQUEST);
    }

    /**
     * 自定义异常
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<R<?>> customException(CustomException e) {
        var msg = String.format("Service [%s]: %s", service, e.getMessage());
        log.info(msg);
        return new ResponseEntity<>(R.fail(e.getMessage()), e.getCode());
    }
}
