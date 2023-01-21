package antifraud.model.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class UserDeleteResponse {
    String username;
    @JsonIgnore
    UserDeleteStatus status;

    @JsonProperty("status")
    String getStatus(){
        return status.getMessage();
    }
}
