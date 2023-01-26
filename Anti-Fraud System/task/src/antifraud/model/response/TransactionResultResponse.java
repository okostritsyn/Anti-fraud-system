package antifraud.model.response;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
public class TransactionResultResponse {
    TransactionResult result;
    String info;
}
