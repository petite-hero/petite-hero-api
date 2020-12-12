package capstone.petitehero.repositories;

import capstone.petitehero.entities.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface ParentRepository extends JpaRepository<Parent, Long> {

    Parent findParentByAccount_Username(String phoneNumber);

    Parent findParentByAccount_UsernameAndIsDisabled(String phoneNumber, Boolean isDisabled);

    @Query(nativeQuery = true, value = "SELECT p.* \n" +
            "FROM petite_hero.child c, petite_hero.parent_child pc, petite_hero.parent p\n" +
            "WHERE c.child_id = :childId AND pc.child_id = c.child_id AND p.parent_id = pc.parent_id")
    Parent getParentByChildID(@Param("childId") Long childId);
}
