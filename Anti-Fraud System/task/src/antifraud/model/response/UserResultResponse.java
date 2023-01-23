package antifraud.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class UserResultResponse {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Long id;
    String name;
    String username;

    public UserResultResponse(Long id, String name, String username) {
        this.id = id;
        this.name = name;
        this.username = username;
    }
}