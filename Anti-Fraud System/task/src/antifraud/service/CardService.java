package antifraud.service;

import antifraud.configuration.TransactionProperties;
import antifraud.mapper.CardMapper;
import antifraud.model.Card;
import antifraud.model.enums.TypeOfLimitsTransaction;
import antifraud.model.enums.TypeOfOperationForLimit;
import antifraud.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.checkdigit.LuhnCheckDigit;
import org.springframework.stereotype.Service;

import java.util.Map;

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
            log.info("find card with number "+currCard.getNumber()+" isStolen "+currCard.isStolen());
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

    public void updateLimitsForCard(Card card, Map<TypeOfLimitsTransaction,TypeOfOperationForLimit> mapOfTypes, Long amount) {
        var maxAllowed = card.getMax_ALLOWED();
        var maxManual = card.getMax_MANUAL();
        log.info("amount for limit:"+amount);
        log.info("current allow limit:"+maxAllowed);
        log.info("current manual limit:"+maxManual);
        log.info("current mapOfTypes limit:"+mapOfTypes.keySet());

        for (TypeOfLimitsTransaction currKey:mapOfTypes.keySet()) {
            var type = mapOfTypes.get(currKey);
            if (currKey == TypeOfLimitsTransaction.MAX_ALLOWED){
                maxAllowed = calculateLimitForType(maxAllowed,type,amount);
            }
            if (currKey == TypeOfLimitsTransaction.MAX_MANUAL_PROCESSING){
                maxManual = calculateLimitForType(maxManual,type,amount);
            }
        }

        card.setMax_ALLOWED(maxAllowed);
        card.setMax_MANUAL(maxManual);

        cardRepository.save(card);
        log.info("Change limits for card with number "+card.getNumber());
        log.info("New limits for card with number "+card.getNumber()+" Max_ALLOWED "+maxAllowed+" Max_MANUAL "+maxManual);
    }

    private long calculateLimitForType(long current_limit, TypeOfOperationForLimit type, Long amount) {
        var new_limit = current_limit;
        if(type == TypeOfOperationForLimit.INCREASE){
            new_limit = (long) Math.ceil(0.8 * current_limit + 0.2 * amount);
        } else if (type == TypeOfOperationForLimit.DECREASE){
            new_limit = (long) Math.ceil(0.8 * current_limit - 0.2 * amount);
        }

        log.info("new limit for type "+type+": "+new_limit);
        log.info("new limit for amount "+amount);
        log.info("new limit for current_limit "+current_limit);

        return new_limit;
    }
}
