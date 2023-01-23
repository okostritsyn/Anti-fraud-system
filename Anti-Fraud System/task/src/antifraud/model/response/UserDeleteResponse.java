package antifraud.model.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter@AllArgsConstructor
public class UserDeleteResponse {
    private String username;
    @JsonIgnore
    private UserDeleteStatus status;

    @JsonProperty("status")
    public String getStatus(){
        return status.getMessage();
    }

    public void setStatus(UserDeleteStatus status){
        this.status = status;
    }
}
