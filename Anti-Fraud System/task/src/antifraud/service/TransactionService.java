package antifraud.service;

import antifraud.model.response.TransactionResult;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    public TransactionResult processTransaction(long amount) {
        if (amount <= 200) { //to do to properties
            return  TransactionResult.ALLOWED;
        }else if (amount <= 1500) {
            return  TransactionResult.MANUAL_PROCESSING;
        } else  {
            return  TransactionResult.PROHIBITED;
        }
    }
}
