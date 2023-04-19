package antifraud.model.response;

import antifraud.model.enums.TransactionResult;
import lombok.Value;

@Value
public class TransactionResultResponse {
    TransactionResult result;
    String info;
}
