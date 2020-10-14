package capstone.petitehero.repositories;

import capstone.petitehero.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Account findAdminByUsernameEqualsAndPasswordEquals(String username, String password);

    Account findAccountByUsername(String username);

    Boolean existsAccountByUsername(String username);
}
