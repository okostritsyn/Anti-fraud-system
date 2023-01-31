package antifraud.mapper;

import antifraud.model.Card;
import antifraud.model.request.CardRequest;
import antifraud.model.response.CardResponse;
import org.springframework.stereotype.Component;

@Component
public class CardMapper {
    public CardResponse mapStolenCardToCardDTO(Card card) {
        return new CardResponse(card.getId(),card.getNumber());
    }

    public Card mapStringNumberToEntity(String number) {
        var card = new Card();
        card.setNumber(number);
        return card;
    }
}
