package antifraud.model.request;


import antifraud.model.Region;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class TransactionRequest {
    @Positive(message = "Amount should be positive")
    @NotNull
    Long amount;
    @NotNull
    @NotEmpty
    String ip;
    @NotNull
    @NotEmpty
    String number;
    @NotNull
    Region region;
    @NotNull
    @JsonFormat(pattern=  "yyyy-MM-dd'T'HH:mm:ss")
    Date date;
}
