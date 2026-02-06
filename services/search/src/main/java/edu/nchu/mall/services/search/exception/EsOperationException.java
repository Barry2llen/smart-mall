package edu.nchu.mall.services.search.exception;

import edu.nchu.mall.components.exception.CustomException;
import org.springframework.http.HttpStatus;

public class EsOperationException extends CustomException {
    public EsOperationException(String message) {
        super(message);
    }
    public EsOperationException(String message, Throwable cause) {
        super(message, cause);
    }
    public EsOperationException(String message, Throwable cause, HttpStatus code) {
        super(message, cause, code);
    }
}
