package antifraud.service;

import antifraud.configuration.TransactionProperties;
import antifraud.model.Card;
import antifraud.repository.StolenCardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StolenCardService {
    private final StolenCardRepository stolenCardRepository;
    private final TransactionProperties props;

    public boolean saveCard(Card card) {
        if (findByNumber(card.getNumber()) != null) {
            return false;
        }
        card.setStolen(true);
        card.setMax_ALLOWED(props.getAllowedAmount());
        card.setMax_MANUAL(props.getManualProcessingAmount());
        stolenCardRepository.save(card);
        log.info("Registered stolen card with number "+card.getNumber());
        return true;
    }


    public List<Card> getListOfCards() {
        return stolenCardRepository.findAll();
    }

    public Card findByNumber(String number){
        return stolenCardRepository.findByNumber(number);
    }

    public boolean deleteCard(String number){
        var card = stolenCardRepository.findByNumber(number);
        if ( card == null) {
            return false;
        }
        card.setStolen(false);
        stolenCardRepository.save(card);
        log.info("Delete stolen card with number "+card.getNumber());
        return true;
    }
}
