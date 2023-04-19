package antifraud.mapper;

import antifraud.model.Card;
import antifraud.model.response.CardResponse;

public class CardMapper {
    public static CardResponse mapStolenCardToCardDTO(Card card) {
        //return new CardResponse(card.getId(),card.getNumber());
        return CustomMapper.INSTANCE.mapStolenCardToCardDTO(card);
    }

    public static Card mapStringNumberToEntity(String number) {
        /*var card = new Card();
        card.setNumber(number);
        return card;*/
        return CustomMapper.INSTANCE.mapStringNumberToEntity(number);

    }
}
