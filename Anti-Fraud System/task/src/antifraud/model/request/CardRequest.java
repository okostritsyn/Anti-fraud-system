package antifraud.model.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class CardRequest {
    @NotNull
    @NotEmpty
    String number;
}

