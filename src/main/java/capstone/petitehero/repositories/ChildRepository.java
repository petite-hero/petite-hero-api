package capstone.petitehero.repositories;

import capstone.petitehero.entities.Child;
import capstone.petitehero.entities.Safezone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChildRepository extends JpaRepository<Child, Long> {

    Child findChildByChildIdEqualsAndIsDisabled(Long childId, Boolean isDisable);
}
