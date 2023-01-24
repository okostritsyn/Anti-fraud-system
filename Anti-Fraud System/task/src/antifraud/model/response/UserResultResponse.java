package antifraud.model.response;

import antifraud.model.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter@Setter
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
