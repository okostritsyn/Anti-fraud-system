package antifraud.repository;

import antifraud.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {
    @Query("SELECT t FROM cards t WHERE t.number = ?1")
    Card findByNumber(String number);

    @Query("SELECT t FROM cards t WHERE t.number = ?1 And t.isStolen = true")
    Card findStolenByNumber(String number);

    @Query("SELECT t FROM cards t WHERE t.isStolen = true")
    List<Card> findAllStolen();
}
