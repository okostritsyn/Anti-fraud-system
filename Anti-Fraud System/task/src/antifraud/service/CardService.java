package antifraud.service;

import antifraud.configuration.TransactionProperties;
import antifraud.mapper.CardMapper;
import antifraud.model.Card;
import antifraud.model.enums.TypeOfOperationForLimit;
import antifraud.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.checkdigit.LuhnCheckDigit;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardService {
    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final TransactionProperties props;

    public boolean validateNumber(String cardNumber) {
        return LuhnCheckDigit.LUHN_CHECK_DIGIT.isValid(cardNumber);
    }

    public Card findCreateCardByNumber(String number) {
        var currCard = cardRepository.findByNumber(number);
        if ( currCard != null) {
            return currCard;
        }
        currCard = cardMapper.mapStringNumberToEntity(number);
        return createCard(currCard);
    }

    public Card createCard(Card card) {
        card.setStolen(false);
        card.setMax_ALLOWED(props.getAllowedAmount());
        card.setMax_MANUAL(props.getManualProcessingAmount());
        cardRepository.save(card);
        log.info("Registered card with number "+card.getNumber());
        return card;
    }

    public void updateLimitsForCard(Card card, TypeOfOperationForLimit type, Long amount) {
        var maxAllowed = calculateLimitForType(card.getMax_ALLOWED(),type,amount);
        var maxManual = calculateLimitForType(card.getMax_MANUAL(),type,amount);

        card.setMax_ALLOWED(maxAllowed);
        card.setMax_MANUAL(maxManual);
        cardRepository.save(card);
        log.info("Change limits for card with number "+card.getNumber());
    }

    private long calculateLimitForType(long current_limit, TypeOfOperationForLimit type, Long amount) {
        var new_limit = current_limit;
        if(type == TypeOfOperationForLimit.INCREASE){
            new_limit = (long) Math.ceil(0.8 * current_limit + 0.2 * amount);
        } else if (type == TypeOfOperationForLimit.DECREASE){
            new_limit = (long) Math.ceil(0.8 * current_limit - 0.2 * amount);
        }
        return new_limit;
    }
}
