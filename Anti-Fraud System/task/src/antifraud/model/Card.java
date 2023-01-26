package antifraud.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity(name = "cards")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name ="number",unique = true)
    private String number;
    @Column(name ="dateOfCreation")
    private LocalDateTime dateOfCreation;

    public Card() {
        this.dateOfCreation = LocalDateTime.now();
    }
}
