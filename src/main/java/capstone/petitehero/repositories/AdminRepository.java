package capstone.petitehero.repositories;

import capstone.petitehero.entities.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    Admin findAdminByUsernameEqualsAndPasswordEquals(String username, String password);
}
