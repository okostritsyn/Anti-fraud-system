package antifraud.service.impl;

import antifraud.configuration.TransactionProperties;
import antifraud.exception.ValidationDTOFailedException;
import antifraud.mapper.CardMapper;
import antifraud.model.Card;
import antifraud.model.enums.TypeOfLimitsTransaction;
import antifraud.model.enums.TypeOfOperationForLimit;
import antifraud.repository.CardRepository;
import antifraud.service.CardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.checkdigit.LuhnCheckDigit;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;
    private final TransactionProperties props;

    private Card createCard(Card card) {
        card.setStolen(false);
        card.setMax_ALLOWED(props.getAllowedAmount());
        card.setMax_MANUAL(props.getManualProcessingAmount());
        cardRepository.save(card);
        log.info("Registered card with number "+card.getNumber());
        return card;
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

    public void updateLimitsForCard(Card card, Map<TypeOfLimitsTransaction,TypeOfOperationForLimit> mapOfTypes, Long amount) {
        var maxAllowed = card.getMax_ALLOWED();
        var maxManual = card.getMax_MANUAL();

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
        log.info("Change limits for card with number "+card.getNumber()+" Max_ALLOWED "+maxAllowed+" Max_MANUAL "+maxManual);
    }

    public void validateNumber(String cardNumber) {
        if (!LuhnCheckDigit.LUHN_CHECK_DIGIT.isValid(cardNumber)){
            throw new ValidationDTOFailedException("Number validate failed!");
        }
    }

    public Card findCreateCardByNumber(String number) {
        var currCard = cardRepository.findByNumber(number);
        if (currCard.isPresent()) {
            return currCard.get();
        }
        var newCard = CardMapper.mapStringNumberToEntity(number);
        return createCard(newCard);
    }

}
