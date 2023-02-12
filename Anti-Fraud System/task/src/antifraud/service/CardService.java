package antifraud.service;

import antifraud.model.Card;
import antifraud.model.enums.TypeOfLimitsTransaction;
import antifraud.model.enums.TypeOfOperationForLimit;

import java.util.Map;

public interface CardService {
    void updateLimitsForCard(Card card, Map<TypeOfLimitsTransaction, TypeOfOperationForLimit> mapOfTypes, Long amount);

    void validateNumber(String cardNumber);

    Card findCreateCardByNumber(String number);
}
