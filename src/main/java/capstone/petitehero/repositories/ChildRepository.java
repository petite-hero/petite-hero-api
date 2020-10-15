package capstone.petitehero.repositories;

import capstone.petitehero.entities.Child;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChildRepository extends JpaRepository<Child, Long> {

    Child findChildByChildIdEqualsAndIsDisabled(Long childId, Boolean isDisable);
}
