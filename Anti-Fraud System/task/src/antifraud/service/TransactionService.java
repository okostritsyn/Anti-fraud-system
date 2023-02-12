package antifraud.service;

import antifraud.model.Transaction;
import antifraud.model.request.TransactionFeedbackRequest;
import antifraud.model.request.TransactionRequest;
import antifraud.model.response.TransactionResponse;
import antifraud.model.response.TransactionResultResponse;
import java.util.List;

public interface TransactionService {

    TransactionResultResponse processTransaction(TransactionRequest req);

    List<TransactionResponse> getTransactionsByNumber(String number);

    List<TransactionResponse> getAllTransactions();

    Transaction setFeedback(TransactionFeedbackRequest transactionFeedback);
}
