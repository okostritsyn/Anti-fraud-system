package antifraud.service.impl;

import antifraud.exception.ConflictRegisterEntityException;
import antifraud.exception.EntityNotExist;
import antifraud.exception.UnprocessableBusinessException;
import antifraud.exception.ValidationDTOFailedException;
import antifraud.mapper.TransactionMapper;
import antifraud.model.Transaction;
import antifraud.model.enums.Region;
import antifraud.model.enums.TypeOfLimitsTransaction;
import antifraud.model.enums.TypeOfOperationForLimit;
import antifraud.model.request.TransactionFeedbackRequest;
import antifraud.model.request.TransactionRequest;
import antifraud.model.enums.TransactionResult;
import antifraud.model.response.TransactionResponse;
import antifraud.model.response.TransactionResultResponse;
import antifraud.repository.TransactionRepository;
import antifraud.service.CardService;
import antifraud.service.IPAddressService;
import antifraud.service.StolenCardService;
import antifraud.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final IPAddressService ipAddressService;
    private final StolenCardService stolenCardService;
    private final CardService cardService;
    private final TransactionRepository transactionRepository;


    public TransactionResultResponse processTransaction(TransactionRequest req) {
        validateTransactionData(req);

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

    public List<TransactionResponse> getTransactionsByNumber(String number) {
        cardService.validateNumber(number);
        var card = cardService.findCreateCardByNumber(number);
        var transactionList = transactionRepository.findByCard(card);

        if (transactionList.isEmpty()) {
            throw new EntityNotExist("Transaction with card number "+number+" not found!");
        }

        var listResponse = new ArrayList<TransactionResponse>();

        for (Transaction currTrans : transactionList) {
            listResponse.add(TransactionMapper.mapTransactionEntityToDTO(currTrans));
        }
        return listResponse;
    }

    public List<TransactionResponse> getAllTransactions() {
        var transList = transactionRepository.findAll();
        var listResponse = new ArrayList<TransactionResponse>();

        for (Transaction currTrans : transList) {
            listResponse.add(TransactionMapper.mapTransactionEntityToDTO(currTrans));
        }
        return listResponse;
    }

    public Transaction setFeedback(TransactionFeedbackRequest transactionFeedback) {
        validateTransactionFeedback(transactionFeedback);
        Transaction currTransaction = getTransactionById(transactionFeedback.getTransactionId());
        checkCanChangeTransactionFeedback(currTransaction,transactionFeedback);
        setFeedbackToTransaction(currTransaction,transactionFeedback.getFeedback());
        updateLimits(currTransaction);
        return currTransaction;
    }

    private TransactionResult getTransactionResultByAmount(Transaction transaction) {
        var amount = transaction.getAmount();

        var maxAllowed = transaction.getCard().getMax_ALLOWED();
        var maxManual = transaction.getCard().getMax_MANUAL();

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
        if (stolenCardService.findStolenByNumber(req.getNumber()).isPresent()) {
            resultTransaction = TransactionResult.PROHIBITED;
        }
        return resultTransaction;
    }

    private TransactionResult checkTransactionIPInBlackList(TransactionRequest req) {
        TransactionResult resultTransaction = TransactionResult.ALLOWED;
        if (ipAddressService.findByAddress(req.getIp()).isPresent()) {
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
        return transactionRepository.findByCardAndDateOfCreationBetween(card,startDate,endDate);
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

    private Transaction saveTransactionToHistory(TransactionRequest req){
        var transaction = TransactionMapper.mapTransactionDTOToEntity(req);
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

    private void validateTransactionData(TransactionRequest req) {
        if (req.getRegion() == Region.INVALID) {
            throw new ValidationDTOFailedException("Region validate failed!");
        }
        if (req.getDate() == null) {
            throw new ValidationDTOFailedException("Date validate failed!");
        }
        ipAddressService.validateIPAddress(req.getIp());
        cardService.validateNumber(req.getNumber());
    }

    private void updateLimits(Transaction currTransaction) {
        var mapOfTypes = getTypeOfOperationForLimit(currTransaction);
        cardService.updateLimitsForCard(currTransaction.getCard(),mapOfTypes, currTransaction.getAmount());
    }

    private void checkCanChangeTransactionFeedback(Transaction currTransaction, TransactionFeedbackRequest transactionFeedback) {
        if (currTransaction.getResult().equals(transactionFeedback.getFeedback())){
            throw new UnprocessableBusinessException("Feedback can not be set!");
        }

        if (currTransaction.getFeedback() != null){
            throw new ConflictRegisterEntityException("Feedback is already set!");
        }
    }

    private void validateTransactionFeedback(TransactionFeedbackRequest transactionFeedback) {
        if (transactionFeedback.getFeedback() == TransactionResult.INVALID){
            throw new ValidationDTOFailedException("Feedback doesn't have right format (ALLOWED, MANUAL_PROCESSING, PROHIBITED)");
        }
    }

    private void setFeedbackToTransaction(Transaction transaction, TransactionResult feedback) {
        transaction.setFeedback(feedback);
        transactionRepository.save(transaction);
        log.info("Update feedback for transaction with card number "+transaction.getCard().getNumber());
    }

    private Transaction getTransactionById(Long transactionId) {
        try {
            return transactionRepository.getReferenceById(transactionId);
        } catch (EntityNotFoundException ex){
            throw new EntityNotExist("Transaction not found!");
        }
    }

    private Map<TypeOfLimitsTransaction,TypeOfOperationForLimit> getTypeOfOperationForLimit(Transaction transaction) {
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

        return returnMap;
    }
}
