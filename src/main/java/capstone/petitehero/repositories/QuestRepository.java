package capstone.petitehero.repositories;

import capstone.petitehero.entities.Quest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestRepository extends JpaRepository<Quest, Long> {

    Quest findQuestByQuestIdAndAndIsDeleted(Long questId, Boolean idDeleted);

    List<Quest> findQuestsByChildChildIdAndIsDeletedOrderByCreatedDateDesc(Long childId, Boolean isDeleted);

    List<Quest> findQuestsByChildChildIdAndAndIsDeletedAndStatusOrderByCreatedDateDesc(Long childId, Boolean isDeleted, String status);

    List<Quest> findQuestsByChildChildIdAndIsDeletedAndStatus(Long childId, Boolean isDeleted, String status);

    List<Quest> findQuestsByIsDeletedAndStatus(Boolean isDeleted, String status);

    @Query(value = "SELECT * FROM petite_hero.quest q WHERE q.assignee_id = :childId " +
            "AND q.is_deleted = :isDeleted " +
            "AND q.status = :status " +
            "AND q.submit_date IS NOT NULL ORDER BY q.submit_date DESC limit :maxBadges", nativeQuery = true)
    List<Quest> findTopQuestsByChild_ChildIdAndIsDeletedAndStatus(@Param("childId") Long childId,
                                                                  @Param("status") String status,
                                                                  @Param("isDeleted") Boolean isDeleted,
                                                                  @Param("maxBadges") Integer maxBadges);
}
