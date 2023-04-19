package antifraud.service.impl;

import antifraud.configuration.TransactionProperties;
import antifraud.exception.EntityNotExist;
import antifraud.exception.ConflictRegisterEntityException;
import antifraud.mapper.CardMapper;
import antifraud.model.Card;
import antifraud.model.request.CardRequest;
import antifraud.model.response.CardResponse;
import antifraud.repository.CardRepository;
import antifraud.service.CardService;
import antifraud.service.StolenCardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class StolenCardServiceImpl implements StolenCardService {
    private final CardRepository cardRepository;
    private final TransactionProperties props;
    private final CardService cardService;

    public Card registerCard(CardRequest req) {
        cardService.validateNumber(req.getNumber());
        if (findStolenByNumber(req.getNumber()).isPresent()) {
            throw new ConflictRegisterEntityException("Card with such number already exist!");
        }
        var cardEntity = cardService.findCreateCardByNumber(req.getNumber());
        saveCard(cardEntity);
        return cardEntity;
    }


    public List<CardResponse> getListOfCards() {
        var cardList = cardRepository.findAllStolen();
        var listResponse = new ArrayList<CardResponse>();

        for (Card card : cardList) {
            var currCard = CardMapper.mapStolenCardToCardDTO(card);
            listResponse.add(currCard);
        }
        return listResponse;
    }

    public Optional<Card> findStolenByNumber(String number){
        return cardRepository.findStolenByNumber(number);
    }

    private void deleteCard(Card card){
        card.setStolen(false);
        cardRepository.save(card);
        log.info("Delete stolen card with number "+card.getNumber());
    }

    public void deleteCardByNumber(String number) {
        cardService.validateNumber(number);
        var card = findStolenByNumber(number);
        if (card.isEmpty()) {
            throw new EntityNotExist("Card not found!");
        }
        deleteCard(card.get());
    }

    private void saveCard(Card card) {
        card.setStolen(true);
        card.setMax_ALLOWED(props.getAllowedAmount());
        card.setMax_MANUAL(props.getManualProcessingAmount());
        cardRepository.save(card);
        log.info("Registered stolen card with number "+card.getNumber());
    }

}
