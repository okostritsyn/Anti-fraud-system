package antifraud.controller;

import antifraud.mapper.TransactionMapper;
import antifraud.model.request.TransactionFeedbackRequest;
import antifraud.model.request.TransactionRequest;
import antifraud.model.response.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import antifraud.service.TransactionService;

import javax.validation.Valid;
import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping("/api/antifraud")
@Slf4j
public class TransactionController {
    TransactionService transactionService;

    @PostMapping(value = "/transaction")
    TransactionResultResponse transaction(@RequestBody @Valid TransactionRequest req) {
        return transactionService.processTransaction(req);
    }

    @PutMapping(value = "/transaction")
    TransactionResponse setFeedback(@RequestBody @Valid TransactionFeedbackRequest transactionFeedback) {
        var currTransaction = transactionService.setFeedback(transactionFeedback);
        return TransactionMapper.mapTransactionEntityToDTO(currTransaction);
    }

    @GetMapping(value = "/history")
    List<TransactionResponse> getAllFromTransactionHistory() {
        return transactionService.getAllTransactions();
    }

    @GetMapping(value = "/history/{number}")
    List<TransactionResponse> getTransactionsFromHistoryByNumber(@PathVariable String number) {
        return transactionService.getTransactionsByNumber(number);
    }
}
