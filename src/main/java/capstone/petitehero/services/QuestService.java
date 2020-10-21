package capstone.petitehero.services;

import capstone.petitehero.dtos.common.Assignee;
import capstone.petitehero.dtos.common.Assigner;
import capstone.petitehero.dtos.response.quest.ListQuestResponseDTO;
import capstone.petitehero.dtos.response.quest.QuestCreateResponseDTO;
import capstone.petitehero.dtos.response.quest.QuestDeleteResponseDTO;
import capstone.petitehero.dtos.response.quest.QuestDetailResponseDTO;
import capstone.petitehero.entities.Quest;
import capstone.petitehero.repositories.QuestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class QuestService {

    @Autowired
    private QuestRepository questRepository;

    public QuestCreateResponseDTO addQuestByParentOrCollaborator(Quest quest) {
        Quest questResult = questRepository.save(quest);

        if (questResult != null) {
            QuestCreateResponseDTO result = new QuestCreateResponseDTO();

            result.setQuestId(questResult.getQuestId());
            result.setName(questResult.getName());
            result.setDescription(questResult.getDescription());

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
            result.setCreatedDate(sdf.format(questResult.getCreatedDate()));

            result.setProgress(questResult.getProgress());
            result.setCriteria(questResult.getCriteria());
            result.setRewardName(questResult.getRewardName());
            result.setRewardPhoto(questResult.getRewardPhoto());
            result.setStatus("CREATED");
            result.setQuestBadge(questResult.getQuestBadge());

            // information of assigner (colaborator or parent)
            Assigner assigner = new Assigner();
            assigner.setPhoneNumber(questResult.getParent().getAccount().getUsername());
            assigner.setFirstName(questResult.getParent().getFirstName());
            assigner.setLastName(questResult.getParent().getLastName());
            if (questResult.getParent().getGender().booleanValue()) {
                assigner.setGender("Male");
            } else {
                assigner.setGender("Female");
            }
            result.setAssigner(assigner);

            // information of assignee (child)
            Assignee assignee = new Assignee();
            assignee.setChildId(questResult.getChild().getChildId());
            assignee.setLastName(questResult.getChild().getLastName());
            assignee.setFirstName(questResult.getChild().getFirstName());
            assignee.setNickName(questResult.getChild().getNickName());
            if (questResult.getChild().getGender().booleanValue()) {
                assignee.setGender("Male");
            } else {
                assignee.setGender("Female");
            }
            result.setAssignee(assignee);
        }
        return null;
    }


    public QuestDetailResponseDTO getDetailOfQuest(Long questId) {
        Quest questResult = questRepository.findQuestByQuestIdAndAndIsDeleted(questId, Boolean.FALSE);

        if (questResult != null) {
            QuestDetailResponseDTO result = new QuestDetailResponseDTO();
            result.setQuestId(questResult.getQuestId());
            result.setName(questResult.getName());
            result.setDescription(questResult.getDescription());

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
            result.setCreatedDate(sdf.format(questResult.getCreatedDate()));

            result.setProgress(questResult.getProgress());
            result.setCriteria(questResult.getCriteria());
            result.setRewardName(questResult.getRewardName());
            result.setRewardPhoto(questResult.getRewardPhoto());
            result.setStatus(questResult.getStatus());
            result.setQuestBadge(questResult.getQuestBadge());

            // information of assigner (colaborator or parent)
            Assigner assigner = new Assigner();
            assigner.setPhoneNumber(questResult.getParent().getAccount().getUsername());
            assigner.setFirstName(questResult.getParent().getFirstName());
            assigner.setLastName(questResult.getParent().getLastName());
            if (questResult.getParent().getGender().booleanValue()) {
                assigner.setGender("Male");
            } else {
                assigner.setGender("Female");
            }
            result.setAssigner(assigner);

            // information of assignee (child)
            Assignee assignee = new Assignee();
            assignee.setChildId(questResult.getChild().getChildId());
            assignee.setLastName(questResult.getChild().getLastName());
            assignee.setFirstName(questResult.getChild().getFirstName());
            assignee.setNickName(questResult.getChild().getNickName());
            if (questResult.getChild().getGender().booleanValue()) {
                assignee.setGender("Male");
            } else {
                assignee.setGender("Female");
            }
            result.setAssignee(assignee);

            return result;
        }
        return null;
    }

    public QuestDeleteResponseDTO deleteQuest(Long questId) {
        Quest quest = questRepository.findQuestByQuestId(questId);

        if (quest != null) {
            quest.setIsDeleted(Boolean.TRUE);
            Quest questResult = questRepository.save(quest);

            if (questResult != null) {
                QuestDeleteResponseDTO result = new QuestDeleteResponseDTO();
                result.setQuestId(questResult.getQuestId());
                result.setStatus("DELETED");

                return result;
            }
        }

        return null;
    }

    public List<ListQuestResponseDTO> getChildListOfQuest(Long childId, String status) {
        List<Quest> listQuestResult;
        if (status != null) {
            listQuestResult = questRepository.findQuestsByChildChildIdAndAndIsDeletedAndStatus(childId, Boolean.FALSE, status);
        } else {
            listQuestResult = questRepository.findQuestsByChildChildIdAndIsDeleted(childId, Boolean.FALSE);
        }
        if (listQuestResult != null) {
            List<ListQuestResponseDTO> result = new ArrayList<>();
            for (Quest questResult: listQuestResult) {
                ListQuestResponseDTO resultData = new ListQuestResponseDTO();

                resultData.setQuestId(questResult.getQuestId());
                resultData.setCriteria(questResult.getCriteria());
                resultData.setProgress(questResult.getProgress());
                resultData.setName(questResult.getName());
                resultData.setQuestBadge(questResult.getQuestBadge());

                result.add(resultData);
            }
            return result;
        }

        return null;
    }
}
