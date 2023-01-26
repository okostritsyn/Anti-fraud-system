package antifraud.service;

import antifraud.configuration.TransactionProperties;
import antifraud.model.request.TransactionRequest;
import antifraud.model.response.TransactionResult;
import antifraud.model.response.TransactionResultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {
    private final IPAddressService ipAddressService;
    private final CardService cardService;
    private final TransactionProperties props;

    private TransactionResult checkTransactionAmount(long amount) {
        if (amount <= props.getAllowedAmount()) {
            return  TransactionResult.ALLOWED;
        }else if (amount <= props.getManualProcessingAmount()) {
            return  TransactionResult.MANUAL_PROCESSING;
        } else  {
            return  TransactionResult.PROHIBITED;
        }
    }

    public TransactionResultResponse processTransaction(TransactionRequest req) {
        StringJoiner joiner = new StringJoiner(",");
        var resultAmount = checkTransactionAmount(req.getAmount());

        if (resultAmount != TransactionResult.ALLOWED) {
            joiner.add("amount");
        }

        TransactionResult resultTransaction = resultAmount;
        if (cardService.findByNumber(req.getNumber()) != null) {
            resultTransaction = TransactionResult.PROHIBITED;
            joiner.add("card-number");
        }

        if (ipAddressService.findByAddress(req.getIp()) != null) {
            resultTransaction = TransactionResult.PROHIBITED;
            joiner.add("ip");
        }

        String[] statuses = joiner.toString().split(",");

        boolean resultEqual = resultTransaction == resultAmount;
        String messageInfo = Arrays.stream(statuses).
                filter(s -> resultEqual || !s.equals("amount")).
                sorted().
                collect(Collectors.joining(", "));

        return new TransactionResultResponse(resultTransaction,messageInfo.isEmpty()?"none":messageInfo);
    }
}
