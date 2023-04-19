package antifraud.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class EntityNotExist extends RuntimeException {
    public EntityNotExist(String message) {
        super(message);
    }
}
