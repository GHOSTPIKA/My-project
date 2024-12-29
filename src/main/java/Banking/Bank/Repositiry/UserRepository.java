package Banking.Bank.Repositiry;

import Banking.Bank.Model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(String email);

    // Add this method to search by username
    Optional<Customer> findByName(String name);
}
