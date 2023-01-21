package antifraud.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter@Setter@AllArgsConstructor
public class UserCreationResultResponse {
    @Positive(message = "ID should be not 0")
    @NotNull
    Long id;
    String userName;
    String name;
}
