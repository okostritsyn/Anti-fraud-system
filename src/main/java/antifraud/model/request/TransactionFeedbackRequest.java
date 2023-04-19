package antifraud.model.request;

import antifraud.model.enums.TransactionResult;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@ToString

public class TransactionFeedbackRequest {
    @NotNull
    private Long transactionId;

    @NotNull
    private TransactionResult feedback;
}
