package antifraud.model.request;

import antifraud.model.enums.Role;
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
public class UserRoleSetRequest {
    @NotNull
    @NotEmpty
    String username;

    @NotNull
    Role role;
}
