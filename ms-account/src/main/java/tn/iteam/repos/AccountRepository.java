package tn.iteam.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.iteam.entities.Account;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, String> {

    List<Account> findByCustomerId(Long customerId);
}
