package antifraud.controller;

import antifraud.mapper.TransactionMapper;
import antifraud.model.enums.Region;
import antifraud.model.Transaction;
import antifraud.model.enums.TransactionResult;
import antifraud.model.request.TransactionFeedbackRequest;
import antifraud.model.request.TransactionRequest;
import antifraud.model.response.*;
import antifraud.service.CardService;
import antifraud.service.IPAddressService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import antifraud.service.TransactionService;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping("/api/antifraud")
@Slf4j
public class TransactionController {
    TransactionService transactionService;
    CardService cardService;
    IPAddressService ipAddressService;

    @PostMapping(value = "/transaction")
    TransactionResultResponse transaction(@RequestBody @Valid TransactionRequest req) {
        log.info("----------POST /transaction "+req);
        validateTransactionData(req);
        return transactionService.processTransaction(req);
    }

    private void validateTransactionData(TransactionRequest req) {
        if (req.getRegion() == Region.INVALID) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Region validate failed!");
        }
        if (req.getDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date validate failed!");
        }

        if (!ipAddressService.validateIPAddress(req.getIp())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "IP validate failed!");
        }
        if (!cardService.validateNumber(req.getNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Number validate failed!");
        }
    }

    @PutMapping(value = "/transaction")
    TransactionResponse putFeedbackToTransaction(@RequestBody @Valid TransactionFeedbackRequest transactionFeedback) {
        log.info("----------PUT /transaction "+transactionFeedback);
        if (transactionFeedback.getFeedback() == TransactionResult.INVALID){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Feedback doesn't have right format (ALLOWED, MANUAL_PROCESSING, PROHIBITED)");
        }

        Transaction currTransaction = transactionService.getTransactionById(transactionFeedback.getTransactionId());
        if (currTransaction == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found!");
        }

        if (currTransaction.getResult().equals(transactionFeedback.getFeedback())){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Feedback can not be set!");
        }

        if (currTransaction.getFeedback() != null){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Feedback is already set!");
        }
        transactionService.setFeedbackToTransaction(currTransaction,transactionFeedback.getFeedback());

        var mapOfTypes = transactionService.getTypeOfOperationForLimit(currTransaction);
        cardService.updateLimitsForCard(currTransaction.getCard(),mapOfTypes, currTransaction.getAmount());
        return TransactionMapper.mapTransactionEntityToDTO(currTransaction);
    }

    @GetMapping(value = "/history")
    List<TransactionResponse> getAllFromHistoryTransaction() {
        var transList = transactionService.getAllTransactions();
        var listResponse = new ArrayList<TransactionResponse>();

        for (Transaction currTrans : transList) {
            listResponse.add(TransactionMapper.mapTransactionEntityToDTO(currTrans));
        }
        return listResponse;
    }

    @GetMapping(value = "/history/{number}")
    List<TransactionResponse> getTransactionFromHistoryByNumber(@PathVariable String number) {
        log.info("----------GET /history "+number);

        if (!cardService.validateNumber(number)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Number validate failed!");
        }
        var transactionList = transactionService.getTransactionByNumber(number);
        if (transactionList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction with card number "+number+" not found!");
        }

        var listResponse = new ArrayList<TransactionResponse>();

        for (Transaction currTrans : transactionList) {
            listResponse.add(TransactionMapper.mapTransactionEntityToDTO(currTrans));
        }
        return listResponse;
    }
}
