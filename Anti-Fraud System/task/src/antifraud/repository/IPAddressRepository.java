package antifraud.repository;

import antifraud.model.IPAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IPAddressRepository extends JpaRepository<IPAddress, Long>{
    @Query("SELECT t FROM ip_addresses t WHERE t.ip = ?1")
    IPAddress findByIP(String ip);
}
