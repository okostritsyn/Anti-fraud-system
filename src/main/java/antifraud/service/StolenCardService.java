package antifraud.service;

import antifraud.model.Card;
import antifraud.model.request.CardRequest;
import antifraud.model.response.CardResponse;

import java.util.List;
import java.util.Optional;

public interface StolenCardService {

    Card registerCard(CardRequest req);

    List<CardResponse> getListOfCards();

    Optional<Card> findStolenByNumber(String number);

    void deleteCardByNumber(String number);
}
