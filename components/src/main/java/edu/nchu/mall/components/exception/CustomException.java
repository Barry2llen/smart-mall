package edu.nchu.mall.components.exception;

import edu.nchu.mall.models.model.R;
import lombok.Getter;
import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException {
  @Getter
  private HttpStatus code = HttpStatus.INTERNAL_SERVER_ERROR;
  @Getter
  private R<?> response = null;
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
  public CustomException(String message, Throwable cause, HttpStatus code, R<?> response) {
    super(message, cause);
    this.code = code;
    this.response = response;
  }
}
