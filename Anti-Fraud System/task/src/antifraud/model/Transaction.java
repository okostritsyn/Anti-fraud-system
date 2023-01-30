package antifraud.model;

import antifraud.model.enums.TransactionResult;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long amount;

    @Column
    private String ip;

    @ManyToOne
    @JoinColumn(name="card_id", nullable=false)
    private Card card;

    @Column
    private String region;

    @Column(name ="dateOfCreation")
    private LocalDateTime dateOfCreation;

    @Column
    @Enumerated(EnumType.STRING)
    private TransactionResult result;

    @Column
    @Enumerated(EnumType.STRING)
    private TransactionResult feedback;

    @Override
    public String toString() {
        return "Transaction{" +
                "ip='" + ip + '\'' +
                ", number='" + card.getNumber() + '\'' +
                ", region=" + region +
                ", dateOfCreation=" + dateOfCreation +
                ", result=" + result +
                ", feedback=" + feedback +
                '}';
    }

    public Transaction() {
        this.dateOfCreation = LocalDateTime.now();
    }

}
