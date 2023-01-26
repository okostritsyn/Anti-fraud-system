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
    private int amount;
    @Column(name ="dateOfCreation")
    private LocalDateTime dateOfCreation;

    public Transaction() {
        this.dateOfCreation = LocalDateTime.now();
    }
}
