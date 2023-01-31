package antifraud.controller;

import antifraud.mapper.CardMapper;
import antifraud.model.Card;
import antifraud.model.request.CardRequest;
import antifraud.model.response.CardResponse;
import antifraud.model.response.StatusResponse;
import antifraud.service.CardService;
import antifraud.service.StolenCardService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/antifraud")
@Slf4j
public class CardController {
    StolenCardService stolenCardService;
    CardService cardService;
    CardMapper cardMapper;

    @PostMapping(value = "/stolencard")
    CardResponse registerCard(@RequestBody @Valid CardRequest req) {
        log.info("----------POST /stolencard "+req);

        if (!cardService.validateNumber(req.getNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Number validate failed!");
        }

        var cardEntity = cardService.findCreateCardByNumber(req.getNumber());
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
}
