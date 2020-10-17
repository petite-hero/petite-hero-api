package capstone.petitehero.repositories;

import capstone.petitehero.entities.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParentRepository extends JpaRepository<Parent, Long> {

    Parent findParentByAccount_Username(String phoneNumber);

}
