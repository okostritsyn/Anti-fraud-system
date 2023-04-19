package antifraud.repository;

import antifraud.model.IPAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface IPAddressRepository extends JpaRepository<IPAddress, Long>{
    @Query("SELECT t FROM ip_addresses t WHERE t.address = ?1")
    Optional<IPAddress> findByIP(String ip);
}
