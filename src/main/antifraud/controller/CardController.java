package antifraud.controller;

import antifraud.mapper.CardMapper;
import antifraud.model.request.CardRequest;
import antifraud.model.response.CardResponse;
import antifraud.model.response.StatusResponse;
import antifraud.service.StolenCardService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/antifraud")
@Slf4j
public class CardController {
    StolenCardService stolenCardService;

    @PostMapping(value = "/stolencard")
    CardResponse registerCard(@RequestBody @Valid CardRequest req) {
        var cardEntity = stolenCardService.registerCard(req);
        return CardMapper.mapStolenCardToCardDTO(cardEntity);
    }

    @GetMapping(value = "/stolencard")
    List<CardResponse> getListOfCards() {
        return stolenCardService.getListOfCards();
    }


    @DeleteMapping(value = "/stolencard/{number}")
    StatusResponse deleteCardByNumber(@PathVariable String number) {
        stolenCardService.deleteCardByNumber(number);
        var message = "Card " + number + " successfully removed!";
        return new StatusResponse(message);
    }
}
