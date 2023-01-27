package antifraud.model.request;

import antifraud.model.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class UserRoleSetRequest {
    @NotNull
    @NotEmpty
    String username;

    @NotNull
    Role role;
}
