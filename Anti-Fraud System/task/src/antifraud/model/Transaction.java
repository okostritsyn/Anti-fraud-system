package antifraud.model;

import antifraud.model.enums.Region;
import antifraud.model.enums.TransactionResult;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("transactionId")
    private Long id;

    @Column
    private Long amount;

    @Column
    private String ip;

    @ManyToOne
    @JoinColumn(name="card_id", nullable=false)
    @JsonIgnore
    private Card card;

    @Column
    private String region;

    @Column(name ="dateOfCreation")
    @JsonProperty("date")
    private LocalDateTime dateOfCreation;

    @Column
    @Enumerated(EnumType.STRING)
    private TransactionResult result;

    @Column
    @Enumerated(EnumType.STRING)
    @JsonIgnore
    private TransactionResult feedback;

    @JsonProperty("feedback")
    public String getStrFeedback(){
        return feedback == null?"":feedback.name();
    }

    @JsonProperty("number")
    public String getNumber(){
        return card.getNumber();
    }

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
