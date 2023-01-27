package antifraud.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Value;

@Value
public class IPAddressResultResponse {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Long id;
    String ip;
}
