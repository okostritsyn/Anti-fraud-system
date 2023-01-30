package antifraud.controller;

import antifraud.mapper.CardMapper;
import antifraud.mapper.IPAddressMapper;
import antifraud.model.IPAddress;
import antifraud.model.enums.Region;
import antifraud.model.Card;
import antifraud.model.Transaction;
import antifraud.model.enums.TypeOfOperationForLimit;
import antifraud.model.request.CardRequest;
import antifraud.model.request.IPAddressRequest;
import antifraud.model.request.TransactionFeedbackRequest;
import antifraud.model.request.TransactionRequest;
import antifraud.model.response.*;
import antifraud.service.CardService;
import antifraud.service.IPAddressService;
import antifraud.service.StolenCardService;
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
    StolenCardService stolenCardService;
    CardService cardService;
    CardMapper cardMapper;
    IPAddressMapper ipAddressMapper;
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

    @PostMapping(value = "/suspicious-ip")
    IPAddressResultResponse registerIP(@RequestBody @Valid IPAddressRequest req) {
        log.info("----------POST /suspicious-ip "+req);

        if (!ipAddressService.validateIPAddress(req.getIp())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "IP validate failed!");
        }

        var IPEntity = ipAddressMapper.mapIPDTOToEntity(req);
        var status = ipAddressService.createIP(IPEntity);

        if (!status) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "IP already exist!");
        }

        return ipAddressMapper.mapIPToIPDTO(IPEntity);
    }

    @GetMapping(value = "/suspicious-ip")
    List<IPAddressResultResponse> getListOfIP() {
        var addressList = ipAddressService.getListOfAddresses();
        var listResponse = new ArrayList<IPAddressResultResponse>();

        for (IPAddress address : addressList) {
            var currIP = ipAddressMapper.mapIPToIPDTO(address);
            listResponse.add(currIP);
        }

        return listResponse;
    }


    @DeleteMapping(value = "/suspicious-ip/{ip}")
    StatusResponse deleteIPAddress(@PathVariable String ip) {
        log.info("----------DELETE /suspicious-ip "+ip);

        if (!ipAddressService.validateIPAddress(ip)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "IP validate failed!");
        }

        var address = ipAddressService.findByAddress(ip);
        boolean status = false;
        if (address != null) status = ipAddressService.deleteAddress(address);
        if (!status) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found!");
        }
        var message = "IP " + ip + " successfully removed!";
        return new StatusResponse(message);
    }


    @PostMapping(value = "/stolencard")
    CardResponse registerCard(@RequestBody @Valid CardRequest req) {
        log.info("----------POST /stolencard "+req);

        if (!cardService.validateNumber(req.getNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Number validate failed!");
        }

        var cardEntity = cardMapper.mapCardDTOToEntity(req);
        var status = stolenCardService.saveCard(cardEntity);

        if (!status) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Card with such number already exist!");
        }

        return cardMapper.mapStolenCardToCardDTO(cardEntity);
    }

    @GetMapping(value = "/stolencard")
    List<CardResponse> getListOfCards() {
        var cardList = stolenCardService.getListOfCards();
        var listResponse = new ArrayList<CardResponse>();

        for (Card card : cardList) {
            var currCard = cardMapper.mapStolenCardToCardDTO(card);
            listResponse.add(currCard);
        }
        return listResponse;
    }


    @DeleteMapping(value = "/stolencard/{number}")
    StatusResponse deleteCardByNumber(@PathVariable String number) {
        log.info("----------DELETE /stolencard "+number);

        if (!cardService.validateNumber(number)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Number validate failed!");
        }

        boolean status = stolenCardService.deleteCard(number);
        if (!status) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Card not found!");
        }

        var message = "Card " + number + " successfully removed!";
        return new StatusResponse(message);
    }

    @PutMapping(value = "/transaction")
    Transaction putFeedbackToTransaction(@RequestBody @Valid TransactionFeedbackRequest transactionFeedback) {
        log.info("----------PUT /transaction "+transactionFeedback);

        Transaction currTransaction = transactionService.getTransactionById(transactionFeedback.getTransactionId());
        if (currTransaction == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found!");
        }

        if (currTransaction.getResult().equals(transactionFeedback.getFeedback())){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Feedback can not be set!");
        }

        if (currTransaction.getFeedback() == null){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Feedback is already set!");
        }
        transactionService.setFeedbackToTransaction(currTransaction,transactionFeedback.getFeedback());

        TypeOfOperationForLimit type = transactionService.getTypeOfOperationForLimit(currTransaction);
        cardService.updateLimitsForCard(currTransaction.getCard(),type, currTransaction.getAmount());

        return currTransaction;
    }

    @GetMapping(value = "/history")
    List<Transaction> getAllFromHistoryTransaction() {
        return transactionService.getAllTransactions();
    }

    @GetMapping(value = "/history/{number}")
    List<Transaction> getTransactionFromHistoryByNumber(@PathVariable String number) {
        log.info("----------GET /history "+number);

        if (!cardService.validateNumber(number)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Number validate failed!");
        }
        var transactionList = transactionService.getTransactionByNumber(number);
        if (transactionList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction with card number "+number+" not found!");
        }

        return transactionList;
    }
}
