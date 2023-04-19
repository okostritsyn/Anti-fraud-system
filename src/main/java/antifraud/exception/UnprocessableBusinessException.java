package antifraud.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
public class UnprocessableBusinessException extends RuntimeException {
    public UnprocessableBusinessException(String message) {
            super(message);
        }
}
