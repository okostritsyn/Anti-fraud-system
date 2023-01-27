package antifraud.model;

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
    private String ip;
    @Column
    private String number;
    @Column
    private Long amount;

    @Column
    private String region;

    @Column(name ="dateOfCreation")
    private LocalDateTime dateOfCreation;

    public Transaction() {
        this.dateOfCreation = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "ip='" + ip + '\'' +
                ", number='" + number + '\'' +
                ", region=" + region +
                ", dateOfCreation=" + dateOfCreation +
                '}';
    }
}
