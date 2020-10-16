package capstone.petitehero.repositories;

import capstone.petitehero.entities.Safezone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SafeZoneRepository extends JpaRepository<Safezone, Long>{
    @Query(nativeQuery = true, value = "SELECT * FROM petite_hero.safezone WHERE petite_hero.safezone.child_id = :childId AND petite_hero.safezone.date = :date")
    public List<Safezone> getListByDate(@Param("childId") Long childId, @Param("date") Long date);
}
