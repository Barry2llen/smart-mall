package edu.nchu.mall.components.advice;

import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;
import java.util.stream.Collectors;

@Hidden
@RestControllerAdvice
@Slf4j
public class GlobalControllerAdvice {

    public record UnknowExceptionResponseMessage(Class<?> exceptionClass, String message) {}

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

    /**
     * 参数校验异常处理
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public R<?> handlerMethodValidationException(HandlerMethodValidationException e){
        return new R<>(RCT.VALIDATION_FAILED, e.getLocalizedMessage(), null);
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
    public ResponseEntity<?> numberFormatException(NumberFormatException e){
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * json格式错误
     */
    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<?> httpMessageNotReadableException(HttpMessageNotReadableException e){
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
