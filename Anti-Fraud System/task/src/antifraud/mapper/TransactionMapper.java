package antifraud.mapper;

import antifraud.configuration.TransactionProperties;
import antifraud.model.Transaction;
import antifraud.model.request.TransactionRequest;
import antifraud.service.CardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

@Component
@Slf4j
@RequiredArgsConstructor
public class TransactionMapper {
    private final CardService cardService;

    public Transaction mapTransactionDTOToEntity(TransactionRequest transactionRequest){
        var transaction = new Transaction();
        transaction.setAmount(transactionRequest.getAmount());
        transaction.setIp(transactionRequest.getIp());
        transaction.setCard(cardService.findCreateCardByNumber(transactionRequest.getNumber()));
        transaction.setRegion(transactionRequest.getRegion().toString());
        transaction.setDateOfCreation(transactionRequest.getDate().toInstant()
                .atZone(ZoneId.of("Z"))
                .toLocalDateTime());
        return transaction;
    }
}
