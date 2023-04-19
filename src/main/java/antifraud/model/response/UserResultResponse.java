package antifraud.model.response;

import antifraud.model.enums.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Value;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Value
public class UserResultResponse {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Long id;
    String name;
    String username;
    @Enumerated(EnumType.STRING)
    Role role;

    public UserResultResponse(Long id, String name, String username, Role role) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.role = role;
    }
}
