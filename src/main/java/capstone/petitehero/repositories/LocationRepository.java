package capstone.petitehero.repositories;

import capstone.petitehero.entities.LocationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<LocationHistory, Long> {
    @Query(nativeQuery = true, value = "SELECT * FROM location_history WHERE child_id = :childId ORDER BY location_history_id DESC LIMIT 1")
    public LocationHistory findLastestLocation(@Param("childId") Long childId);

    @Query(nativeQuery = true, value = "SELECT * FROM location_history WHERE child_id = :childId AND time >= :from && time <= :to")
    public List<LocationHistory> getListByTime(@Param("childId") Long childId, @Param("from") Long from, @Param("to") Long to);
}
