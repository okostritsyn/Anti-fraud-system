package antifraud.repository;

import antifraud.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT t FROM users t WHERE t.username = ?1")
    User findByName(String name);
}
