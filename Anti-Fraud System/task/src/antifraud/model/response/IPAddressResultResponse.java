package antifraud.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class IPAddressResultResponse {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Long id;
    String ip;
}
