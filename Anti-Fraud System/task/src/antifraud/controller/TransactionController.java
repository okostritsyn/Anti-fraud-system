package antifraud.controller;

import antifraud.model.request.TransactionRequest;
import antifraud.model.response.*;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import antifraud.service.TransactionService;

import javax.validation.Valid;


@RestController
@AllArgsConstructor
@RequestMapping("/api/antifraud")
public class TransactionController {
    TransactionService transactionService;

    @PostMapping(value ="/transaction")
    TransactionResultResponse transaction(@RequestBody @Valid TransactionRequest req){
        var result = transactionService.processTransaction(req.getAmount());
        return new TransactionResultResponse(result);
    }
}
