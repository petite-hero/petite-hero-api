package capstone.petitehero.repositories;

import capstone.petitehero.entities.LocationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<LocationHistory, Long> {
    @Query(nativeQuery = true, value = "SELECT * FROM location_history WHERE child_id = :childId ORDER BY location_history_id DESC LIMIT 1")
    LocationHistory findLatestLocation(@Param("childId") Long childId);

    @Query(nativeQuery = true, value = "SELECT * FROM location_history WHERE child_id = :childId AND time >= :from && time <= :to")
    List<LocationHistory> getListByTime(@Param("childId") Long childId, @Param("from") Long from, @Param("to") Long to);

//    @Query(nativeQuery = true, value = "" +
//            "SELECT p.id as parent_id, CONCAT(p.first_name, ' ', p.last_name) AS parent_name, c.child_id, CONCAT(c.first_name, ' ' ,c.last_name) AS child_name, p.push_token as parent_push_token, c.push_token as child_push_token\n" +
//            "FROM parent p, child c,\n" +
//            "\t(SELECT pc.parent_id, pc.child_id\n" +
//            "\tFROM parent_child pc \n" +
//            "\tWHERE pc.child_id = :childId) pc\n" +
//            "WHERE pc.parent_id = p.id AND pc.child_id = c.child_id")
//    public List<ParentChildPushTokenDTO> getParentPushToken(@Param("childId") Long childId);

    @Query(nativeQuery = true, value = "" +
            "SELECT p.push_token\n" +
            "FROM parent p,\n" +
            "\t(SELECT pc.parent_id\n" +
            "\tFROM parent_child pc \n" +
            "\tWHERE pc.child_id = :childId) pc\n" +
            "WHERE pc.parent_id = p.parent_id")
    ArrayList<String> getParentPushToken(@Param("childId") Long childId);

    @Transactional
    @Modifying
    @Query(value = "Alter Table location_history Auto_Increment = 1", nativeQuery = true)
    void resetGeneratedIdInLocationHistoryTable();
}
