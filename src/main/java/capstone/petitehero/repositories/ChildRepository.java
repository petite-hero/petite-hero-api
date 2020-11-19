package capstone.petitehero.repositories;

import capstone.petitehero.entities.Child;
import capstone.petitehero.entities.Safezone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public interface ChildRepository extends JpaRepository<Child, Long> {

    Child findChildByChildIdEqualsAndIsDisabled(Long childId, Boolean isDisable);

    @Query(nativeQuery = true, value = "" +
            "SELECT DISTINCT c.child_id, c.push_token\n" +
            "FROM petite_hero.safezone s, petite_hero.child c\n" +
            "WHERE s.is_disabled = FALSE \n" +
            "\tAND ((s.date >= :currentDate) OR (s.repeat_on IS NOT NULL))\n" +
            "\tAND s.child_id = c.child_id;")
    public List<Object[]> getChildListBySafeZones (@Param("currentDate") Long currentDate);

    List<Child> findChildrenByIsDisabled(Boolean isDisabled);
}
