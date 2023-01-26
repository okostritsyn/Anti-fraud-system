package antifraud.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CardResponse {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Long id;
    String number;
}
