package antifraud.model.request;

import antifraud.model.enums.UserAccessOperation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class UserAccessRequest {
    @NotNull
    @NotEmpty
    String username;

    @NotNull
    UserAccessOperation operation;
}
