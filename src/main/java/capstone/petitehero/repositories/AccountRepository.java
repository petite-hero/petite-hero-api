package capstone.petitehero.repositories;

import capstone.petitehero.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Account findAdminByUsernameEqualsAndPasswordEquals(String username, String password);

    List<Account> findAccountsByRole(String role);

    Boolean existsAccountByUsername(String username);

    Account findAccountByUsernameAndAndRole(String username, String role);

    Account findAccountByUsername(String username);
}
