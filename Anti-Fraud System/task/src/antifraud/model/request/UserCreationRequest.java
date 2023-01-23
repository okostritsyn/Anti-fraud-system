package antifraud.model.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class UserCreationRequest {
    @NotNull
    String name;
    @NotNull
    String username;
    @NotNull
    String password;
}
