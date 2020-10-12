package capstone.petitehero.repositories;

import capstone.petitehero.entities.Quest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestRepository extends JpaRepository<Quest, Long> {

    Quest findQuestByQuestId(Long questId);

    Quest findQuestByQuestIdAndAndIsDeleted(Long questId, Boolean idDeleted);

    List<Quest> findQuestsByChildChildIdAndIsDeleted(Long childId, Boolean isDeleted);

    List<Quest> findQuestsByChildChildIdAndAndIsDeletedAndStatus(Long childId, Boolean isDeleted, String status);
}
