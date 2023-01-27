package antifraud.model.response;

import lombok.Value;

@Value
public class TransactionResultResponse {
    TransactionResult result;
    String info;
}
