package antifraud.service;

import antifraud.model.Card;
import antifraud.model.request.CardRequest;
import antifraud.model.response.CardResponse;
import antifraud.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.checkdigit.LuhnCheckDigit;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;

    public boolean validateNumber(String cardNumber) {
        return LuhnCheckDigit.LUHN_CHECK_DIGIT.isValid(cardNumber);
    }

    public Card mapCardDTOToEntity(CardRequest req) {
        var card = new Card();
        card.setNumber(req.getNumber());
        return card;
    }

    public boolean saveCard(Card card) {
        if (cardRepository.findByNumber(card.getNumber()) != null) {
            return false;
        }
        cardRepository.save(card);
        log.info("Registered card with number "+card.getNumber());
        return true;
    }

    public CardResponse mapCardToCardDTO(Card card) {
        return new CardResponse(card.getId(),card.getNumber());
    }

    public List<Card> getListOfCards() {
        return cardRepository.findAll();
    }

    public Card findByNumber(String number){
        return cardRepository.findByNumber(number);
    }

    public boolean deleteCard(Card card){
        boolean status = false;
        try {
            cardRepository.delete(card);
            status = true;
        }catch (OptimisticLockingFailureException ex) {
            log.error("An error while delete card with number "+card.getNumber(),ex);
        }
        return status;
    }
}
