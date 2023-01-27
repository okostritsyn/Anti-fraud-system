package antifraud.service;

import antifraud.configuration.TransactionProperties;
import antifraud.model.Transaction;
import antifraud.model.request.TransactionRequest;
import antifraud.model.response.TransactionResult;
import antifraud.model.response.TransactionResultResponse;
import antifraud.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {
    private final IPAddressService ipAddressService;
    private final CardService cardService;
    private final TransactionProperties props;
    private final TransactionRepository transactionRepository;

    private TransactionResult checkTransactionAmount(long amount) {
        if (amount <= props.getAllowedAmount()) {
            return  TransactionResult.ALLOWED;
        }else if (amount <= props.getManualProcessingAmount()) {
            return  TransactionResult.MANUAL_PROCESSING;
        } else  {
            return  TransactionResult.PROHIBITED;
        }
    }

    private TransactionResult checkTransactionCardNumberInBlackList(TransactionRequest req) {
        TransactionResult resultTransaction = TransactionResult.ALLOWED;
        if (cardService.findByNumber(req.getNumber()) != null) {
            resultTransaction = TransactionResult.PROHIBITED;
        }
        return resultTransaction;
    }

    private TransactionResult checkTransactionIPInBlackList(TransactionRequest req) {
        TransactionResult resultTransaction = TransactionResult.ALLOWED;
        if (ipAddressService.findByAddress(req.getIp()) != null) {
            resultTransaction = TransactionResult.PROHIBITED;
        }
        return resultTransaction;
    }

    private List<Transaction> getListOfTransactionFromHistory(TransactionRequest req) {
        var endDate = req.getDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        var startDate = endDate.minusHours(1);
        return transactionRepository.findByNumberAndDateOfCreationBetween(req.getNumber(),startDate,endDate);
    }

    private TransactionResult checkTransactionFieldInHistory(List<Transaction> transactions, Function<Transaction,String> field) {
        TransactionResult resultTransaction = TransactionResult.ALLOWED;
        var count = transactions.stream().map(field).distinct().count();
        if (count > 3) {
            resultTransaction = TransactionResult.PROHIBITED;
        } else if (count == 3) {
            resultTransaction = TransactionResult.MANUAL_PROCESSING;
        }
        return resultTransaction;
    }

    private void saveTransactionInHistory(TransactionRequest req){
        var transaction = mapTransactionDTOToEntity(req);
        transactionRepository.save(transaction);
        log.info("Create transaction with card number "+transaction.getNumber());
    }

    private Transaction mapTransactionDTOToEntity(TransactionRequest transactionRequest){
        var transaction = new Transaction();
        transaction.setAmount(transactionRequest.getAmount());
        transaction.setIp(transactionRequest.getIp());
        transaction.setNumber(transactionRequest.getNumber());
        transaction.setRegion(transactionRequest.getRegion().toString());
        transaction.setDateOfCreation(transactionRequest.getDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime());
        return transaction;
    }

    public TransactionResultResponse processTransaction(TransactionRequest req) {
        saveTransactionInHistory(req);
        List<Transaction> listOfTransactionFromHistory = getListOfTransactionFromHistory(req);

        StringJoiner joiner = new StringJoiner(",");
        var resultAmount = checkTransactionAmount(req.getAmount());

        if (resultAmount != TransactionResult.ALLOWED) {
            joiner.add("amount");
        }

        TransactionResult currResult = checkTransactionCardNumberInBlackList(req);
        TransactionResult resultTransaction = resultAmount;
        if (currResult != TransactionResult.ALLOWED) {
            resultTransaction = currResult;
            joiner.add("card-number");
        }

        currResult = checkTransactionIPInBlackList(req);
        if (currResult != TransactionResult.ALLOWED) {
            resultTransaction = currResult;
            joiner.add("ip");
        }

        currResult = checkTransactionFieldInHistory(listOfTransactionFromHistory,Transaction::getIp);
        if (currResult != TransactionResult.ALLOWED) {
            resultTransaction = currResult;
            joiner.add("ip-correlation");
        }

        currResult = checkTransactionFieldInHistory(listOfTransactionFromHistory,Transaction::getRegion);
        if (currResult != TransactionResult.ALLOWED) {
            resultTransaction = currResult;
            joiner.add("region-correlation");
        }

        String[] statuses = joiner.toString().split(",");

        boolean resultEqual = resultTransaction == resultAmount;
        String messageInfo = Arrays.stream(statuses).
                filter(s -> resultEqual || !s.equals("amount")).
                sorted().
                collect(Collectors.joining(", "));

        messageInfo = messageInfo.isEmpty()?"none":messageInfo;

        return new TransactionResultResponse(resultTransaction,messageInfo);
    }

}
