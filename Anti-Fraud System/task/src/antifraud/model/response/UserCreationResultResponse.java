package antifraud.model.response;

import lombok.Value;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Value
public class UserCreationResultResponse {
    @Positive(message = "ID should be not 0")
    @NotNull
    Long id;
    String userName;
    String name;
}
