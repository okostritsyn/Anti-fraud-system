package antifraud.service;

import antifraud.mapper.TransactionMapper;
import antifraud.model.Transaction;
import antifraud.model.enums.TypeOfLimitsTransaction;
import antifraud.model.enums.TypeOfOperationForLimit;
import antifraud.model.request.TransactionRequest;
import antifraud.model.enums.TransactionResult;
import antifraud.model.response.TransactionResultResponse;
import antifraud.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {
    private final IPAddressService ipAddressService;
    private final StolenCardService stolenCardService;
    private final TransactionMapper transactionMapper;
    private final CardService cardService;
    private final TransactionRepository transactionRepository;

    private TransactionResult getTransactionResultByAmount(Transaction transaction) {
        var amount = transaction.getAmount();

        var maxAllowed = transaction.getCard().getMax_ALLOWED();
        var maxManual = transaction.getCard().getMax_MANUAL();

        log.info("maxAllowed limit:"+maxAllowed);
        log.info("maxManual limit:"+maxManual);

        if (amount <= maxAllowed) {
            return  TransactionResult.ALLOWED;
        }else if (amount <= maxManual) {
            return  TransactionResult.MANUAL_PROCESSING;
        } else  {
            return  TransactionResult.PROHIBITED;
        }
    }

    private TransactionResult checkTransactionCardNumberInBlackList(TransactionRequest req) {
        TransactionResult resultTransaction = TransactionResult.ALLOWED;
        if (stolenCardService.findByNumber(req.getNumber()) != null) {
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
        var card = cardService.findCreateCardByNumber(req.getNumber());
        var endDate = req.getDate().toInstant()
                .atZone(ZoneId.of("Z"))
                .toLocalDateTime();
        var startDate = endDate.minusHours(1);
        log.info("------------try to find transaction by card "+card.getNumber()+" and date "+startDate+" end date "+endDate);
        return transactionRepository.findByCardAndDateOfCreationBetween(card,startDate,endDate);
    }

    private TransactionResult checkTransactionFieldInHistory(List<Transaction> transactions, Function<Transaction,String> field) {
        TransactionResult resultTransaction = TransactionResult.ALLOWED;
        log.info("----------list of transactions "+transactions);
        var count = transactions.stream().map(field).distinct().count();
        if (count > 3) {
            resultTransaction = TransactionResult.PROHIBITED;
        } else if (count == 3) {
            resultTransaction = TransactionResult.MANUAL_PROCESSING;
        }
        return resultTransaction;
    }

    private Transaction saveTransactionToHistory(TransactionRequest req){
        var transaction = transactionMapper.mapTransactionDTOToEntity(req);
        var card = cardService.findCreateCardByNumber(req.getNumber());
        transaction.setCard(card);
        transactionRepository.save(transaction);
        log.info("Create transaction with card number "+card.getNumber());
        return transaction;
    }

    private void addTransactionResultToHistory(Transaction transaction, TransactionResult result){
        transaction.setResult(result);
        transactionRepository.save(transaction);
        log.info("Update result for transaction with card number "+transaction.getCard().getNumber());
    }

    public TransactionResultResponse processTransaction(TransactionRequest req) {
        var transaction = saveTransactionToHistory(req);
        List<Transaction> listOfTransactionFromHistory = getListOfTransactionFromHistory(req);

        StringJoiner joiner = new StringJoiner(",");
        var transactionResultByAmount = getTransactionResultByAmount(transaction);

        if (transactionResultByAmount != TransactionResult.ALLOWED) {
            joiner.add("amount");
        }

        TransactionResult currResult = checkTransactionCardNumberInBlackList(req);
        TransactionResult resultTransaction = transactionResultByAmount;
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

        boolean resultEqual = resultTransaction == transactionResultByAmount;
        String messageInfo = Arrays.stream(statuses).
                filter(s -> resultEqual || !s.equals("amount")).
                sorted().
                collect(Collectors.joining(", "));

        messageInfo = messageInfo.isEmpty()?"none":messageInfo;

        addTransactionResultToHistory(transaction,resultTransaction);

        return new TransactionResultResponse(resultTransaction,messageInfo);
    }

    public List<Transaction> getTransactionByNumber(String number) {
        var card = cardService.findCreateCardByNumber(number);
        return transactionRepository.findByCard(card);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public void setFeedbackToTransaction(Transaction transaction, TransactionResult feedback) {
        transaction.setFeedback(feedback);
        transactionRepository.save(transaction);
        log.info("Update feedback for transaction with card number "+transaction.getCard().getNumber());
    }

    public Transaction getTransactionById(Long transactionId) {
        return transactionRepository.getReferenceById(transactionId);
    }

    public Map<TypeOfLimitsTransaction,TypeOfOperationForLimit> getTypeOfOperationForLimit(Transaction transaction) {
        var returnMap = new HashMap<TypeOfLimitsTransaction,TypeOfOperationForLimit>();
        if (transaction.getFeedback() == TransactionResult.ALLOWED){
            returnMap.put(TypeOfLimitsTransaction.MAX_ALLOWED,TypeOfOperationForLimit.INCREASE);
            returnMap.put(TypeOfLimitsTransaction.MAX_MANUAL_PROCESSING,
                    transaction.getResult() == TransactionResult.PROHIBITED?TypeOfOperationForLimit.INCREASE:TypeOfOperationForLimit.NOCHANGE);
        }

        if (transaction.getFeedback() == TransactionResult.PROHIBITED){
            returnMap.put(TypeOfLimitsTransaction.MAX_MANUAL_PROCESSING,TypeOfOperationForLimit.DECREASE);
            returnMap.put(TypeOfLimitsTransaction.MAX_ALLOWED,
                    transaction.getResult() == TransactionResult.ALLOWED?TypeOfOperationForLimit.DECREASE:TypeOfOperationForLimit.NOCHANGE);
        }

        if (transaction.getFeedback() == TransactionResult.MANUAL_PROCESSING){
            returnMap.put(TypeOfLimitsTransaction.MAX_ALLOWED,
                    transaction.getResult() == TransactionResult.ALLOWED?TypeOfOperationForLimit.DECREASE:TypeOfOperationForLimit.NOCHANGE);
            returnMap.put(TypeOfLimitsTransaction.MAX_MANUAL_PROCESSING,
                    transaction.getResult() == TransactionResult.PROHIBITED?TypeOfOperationForLimit.INCREASE:TypeOfOperationForLimit.NOCHANGE);
        }

        log.info("curr feedback "+transaction.getFeedback() );
        log.info("curr result "+transaction.getResult() );
        log.info("Map for change limits "+returnMap);
        return returnMap;
    }
}
