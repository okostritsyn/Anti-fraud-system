package antifraud.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity(name = "ip_addresses")
public class IPAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name ="ip",unique = true)
    private String ip;
    @Column(name ="dateOfCreation")
    private LocalDateTime dateOfCreation;

    public IPAddress() {
        this.dateOfCreation = LocalDateTime.now();
    }

}
