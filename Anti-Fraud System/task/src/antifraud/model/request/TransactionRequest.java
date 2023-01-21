package antifraud.model.request;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Positive;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class TransactionRequest {
    @Positive(message = "Amount should be positive")
    @NotNull
    Long amount;
}
