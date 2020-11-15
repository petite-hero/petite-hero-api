package capstone.petitehero.repositories;

import capstone.petitehero.entities.Safezone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SafeZoneRepository extends JpaRepository<Safezone, Long>{

    @Query(nativeQuery = true, value = "" +
            "SELECT *\n" +
            "FROM petite_hero.safezone s\n" +
            "WHERE s.child_id = :childId \n" +
            "\tAND s.is_disabled = FALSE \n" +
            "\tAND s.repeat_on REGEXP :regex\n" +
            "UNION\n" +
            "SELECT *\n" +
            "FROM petite_hero.safezone s\n" +
            "WHERE s.child_id = :childId \n" +
            "\tAND s.is_disabled = FALSE \n" +
            "\tAND s.repeat_on IS NULL \n" +
            "\tAND s.date = :currentDate")
    List<Safezone> getListSafeZone(@Param("childId") Long childId, @Param("currentDate") Long currentDate, @Param("regex") String regex);
}
