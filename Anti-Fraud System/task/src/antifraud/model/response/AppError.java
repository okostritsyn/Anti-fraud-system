package antifraud.model.response;

import lombok.Value;

@Value
public class AppError {
    public String error;

    public AppError(String msg) {
        this.error = msg;
    }
}