package edu.nchu.mall.components.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException {
  @Getter
  private HttpStatus code = HttpStatus.INTERNAL_SERVER_ERROR;
  public CustomException(String message) {
    super(message);
  }
  public CustomException(String message, Throwable cause) {
    super(message, cause);
  }
  public CustomException(String message, Throwable cause, HttpStatus code) {
    super(message, cause);
    this.code = code;
  }
}
