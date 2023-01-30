package antifraud.model.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter@Setter@NoArgsConstructor
public class TransactionResponse {
    private Long transactionId;
    private Long amount;
    private String ip;
    private String number;
    private String region;
    private LocalDateTime date;
    private String result;
    private String feedback;
}
