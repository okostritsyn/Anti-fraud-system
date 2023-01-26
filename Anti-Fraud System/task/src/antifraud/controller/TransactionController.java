package antifraud.controller;

import antifraud.model.Card;
import antifraud.model.IPAddress;
import antifraud.model.Region;
import antifraud.model.request.CardRequest;
import antifraud.model.request.IPAddressRequest;
import antifraud.model.request.TransactionRequest;
import antifraud.model.response.*;
import antifraud.service.CardService;
import antifraud.service.IPAddressService;
import lombok.AllArgsConstructor;
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
public class TransactionController {
    TransactionService transactionService;
    CardService cardService;
    IPAddressService ipAddressService;

    @PostMapping(value ="/transaction")
    TransactionResultResponse transaction(@RequestBody @Valid TransactionRequest req){
        if (req.getRegion() == Region.INVALID) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Region validate failed!");
        }
        if (req.getDate() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Date validate failed!");
        }

        if (!ipAddressService.validateIPAddress(req.getIp())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "IP validate failed!");
        }
        if (!cardService.validateNumber(req.getNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Number validate failed!");
        }

       return transactionService.processTransaction(req);
    }

    @PostMapping(value ="/suspicious-ip")
    IPAddressResultResponse registerIP(@RequestBody @Valid IPAddressRequest req){
        if (!ipAddressService.validateIPAddress(req.getIp())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"IP validate failed!");
        }

        var IPEntity = ipAddressService.mapIPDTOToEntity(req);
        var status = ipAddressService.createIP(IPEntity);

        if (!status){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"IP already exist!");
        }

        return ipAddressService.mapIPToIPDTO(IPEntity);
    }

    @GetMapping(value ="/suspicious-ip")
    List<IPAddressResultResponse> getListOfIP(){
        var addressList = ipAddressService.getListOfAddresses();
        var listResponse =  new ArrayList<IPAddressResultResponse>();

        for (IPAddress address:addressList) {
            var currIP = ipAddressService.mapIPToIPDTO(address);
            listResponse.add(currIP);
        }

        return listResponse;
    }


    @DeleteMapping(value ="/suspicious-ip/{ip}")
    StatusResponse deleteIPAddress(@PathVariable String ip){
        if (!ipAddressService.validateIPAddress(ip)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"IP validate failed!");
        }

        var address = ipAddressService.findByAddress(ip);
        boolean status = false;
        if (address != null) status = ipAddressService.deleteAddress(address);
        if (!status){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Address not found!");
        }
        var message = "IP "+ip+" successfully removed!";
        return new StatusResponse(message);
    }


    @PostMapping(value ="/stolencard")
    CardResponse registerCard(@RequestBody @Valid CardRequest req){
        if (!cardService.validateNumber(req.getNumber())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Number validate failed!");
        }

        var cardEntity = cardService.mapCardDTOToEntity(req);
        var status = cardService.saveCard(cardEntity);

        if (!status){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Card with such number already exist!");
        }

        return cardService.mapCardToCardDTO(cardEntity);
    }

    @GetMapping(value ="/stolencard")
    List<CardResponse> getListOfCards(){
        var cardList = cardService.getListOfCards();
        var listResponse =  new ArrayList<CardResponse>();

        for (Card card:cardList) {
            var currCard = cardService.mapCardToCardDTO(card);
            listResponse.add(currCard);
        }
        return listResponse;
    }


    @DeleteMapping(value ="/stolencard/{number}")
    StatusResponse deleteCardByNumber(@PathVariable String number){
        if (!cardService.validateNumber(number)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Number validate failed!");
        }

        var card = cardService.findByNumber(number);
        boolean status = false;
        if (card != null) status = cardService.deleteCard(card);
        if (!status){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Card not found!");
        }
        var message = "Card "+number+" successfully removed!";
        return new StatusResponse(message);
    }
}
