package antifraud.repository;

import antifraud.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;


public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByNumberAndDateOfCreationBetween(String number, LocalDateTime dateOfCreation, LocalDateTime dateOfCreation2);
}
