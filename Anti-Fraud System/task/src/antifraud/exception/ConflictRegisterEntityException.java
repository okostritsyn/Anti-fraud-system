package antifraud.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class ConflictRegisterEntityException extends RuntimeException{
    public ConflictRegisterEntityException(String message) {
        super(message);
    }
}
