package capstone.petitehero.repositories;

import capstone.petitehero.entities.Parent;
import capstone.petitehero.entities.Parent_Child;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParentChildRepository extends JpaRepository<Parent_Child, Long> {

    Parent_Child findParent_ChildByChild_ChildId(Long childId);
}
