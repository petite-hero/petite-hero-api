package capstone.petitehero.repositories;

import capstone.petitehero.entities.Quest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestRepository extends JpaRepository<Quest, Long> {

    Quest findQuestByQuestIdAndAndIsDeleted(Long questId, Boolean idDeleted);

    List<Quest> findQuestsByChildChildIdAndIsDeletedOrderByCreatedDateDesc(Long childId, Boolean isDeleted);

    List<Quest> findQuestsByChildChildIdAndAndIsDeletedAndStatusOrderByCreatedDateDesc(Long childId, Boolean isDeleted, String status);

    List<Quest> findQuestsByChildChildIdAndIsDeletedAndStatus(Long childId, Boolean isDeleted, String status);
}
