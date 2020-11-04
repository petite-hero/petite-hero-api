package capstone.petitehero.services;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.common.Assignee;
import capstone.petitehero.dtos.common.Assigner;
import capstone.petitehero.dtos.response.quest.*;
import capstone.petitehero.dtos.response.quest.badge.QuestBadgeResponseDTO;
import capstone.petitehero.entities.Quest;
import capstone.petitehero.repositories.QuestRepository;
import capstone.petitehero.utilities.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestService {

    @Autowired
    private QuestRepository questRepository;

    public QuestCreateResponseDTO addQuestByParentOrCollaborator(Quest quest, MultipartFile rewardPhoto) {
        Quest questResult = questRepository.save(quest);

        if (questResult != null) {
            questResult.setRewardPhoto(Util.saveImageToSystem(
                    questResult.getQuestId().toString(),
                    "Reward_Image",
                    rewardPhoto));

            // save reward image to db
            questRepository.save(questResult);

            QuestCreateResponseDTO result = new QuestCreateResponseDTO();

            result.setQuestId(questResult.getQuestId());
            result.setName(questResult.getName());
            result.setDescription(questResult.getDescription());
            result.setQuestBadgeId(questResult.getQuestBadge());

            result.setCreatedDate(Util.formatTimestampToDateTime(questResult.getCreatedDate()));

            result.setStatus(Constants.status.ASSIGNED.toString());

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


    public QuestDetailResponseDTO getDetailOfQuest(Long questId, String role) {
        Quest questResult = questRepository.findQuestByQuestIdAndAndIsDeleted(questId, Boolean.FALSE);

        if (questResult != null) {
            QuestDetailResponseDTO result = new QuestDetailResponseDTO();
            result.setQuestId(questResult.getQuestId());
            result.setName(questResult.getName());
            result.setDescription(questResult.getDescription());
            result.setCreatedDate(questResult.getCreatedDate());
            result.setStatus(questResult.getStatus());
            result.setQuestBadgeId(questResult.getQuestBadge());
            if (role.equals(Constants.PARENT)) {
                result.setRewardName(questResult.getRewardName());
                result.setRewardPhoto(Util.fromImageFileToBase64String(questResult.getRewardPhoto()));
            }

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

    public QuestStatusResponseDTO deleteQuest(Quest quest) {
        if (quest != null) {
            quest.setIsDeleted(Boolean.TRUE);
            Quest questResult = questRepository.save(quest);

            if (questResult != null) {
                QuestStatusResponseDTO result = new QuestStatusResponseDTO();
                result.setQuestId(questResult.getQuestId());
                result.setStatus(Constants.status.DELETED.toString());

                return result;
            }
        }

        return null;
    }

    public List<ListQuestResponseDTO> getChildListOfQuest(Long childId, String status) {
        List<Quest> listQuestResult;
        if (status != null) {
            listQuestResult = questRepository.findQuestsByChildChildIdAndAndIsDeletedAndStatusOrderByCreatedDateDesc(childId, Boolean.FALSE, status);
        } else {
            listQuestResult = questRepository.findQuestsByChildChildIdAndIsDeletedOrderByCreatedDateDesc(childId, Boolean.FALSE);
        }
        if (listQuestResult != null) {
            List<ListQuestResponseDTO> result = new ArrayList<>();
            for (Quest questResult: listQuestResult) {
                ListQuestResponseDTO resultData = new ListQuestResponseDTO();

                resultData.setQuestId(questResult.getQuestId());
                resultData.setName(questResult.getName());
                resultData.setStatus(questResult.getStatus());

                if (questResult.getQuestBadge() != null) {
                    resultData.setQuestBadgeId(questResult.getQuestBadge());
                }

                result.add(resultData);
            }
            return result;
        }

        return null;
    }


    public List<QuestBadgeResponseDTO> getBadgeListChildArchived(Long childId) {
        List<Quest> listQuestResult = questRepository.findQuestsByChildChildIdAndIsDeletedAndStatus(childId, Boolean.FALSE, "FINISHED");

        if (listQuestResult != null) {
            List<QuestBadgeResponseDTO> result = new ArrayList<>();

            List<Quest> filterQuestBadgeList = listQuestResult.stream()
                    .filter(Util.distinctByKey(Quest::getQuestBadge)).collect(Collectors.toList());

            for (Quest questBadgeDistinct : filterQuestBadgeList) {
                QuestBadgeResponseDTO dataResult = new QuestBadgeResponseDTO();
                dataResult.setQuestBadgeId(questBadgeDistinct.getQuestBadge());

                result.add(dataResult);
            }

            for (QuestBadgeResponseDTO questBadgeDistinct : result) {
                Long questCompleted = listQuestResult.stream()
                        .filter(quest -> quest.getQuestBadge().equals(questBadgeDistinct.getQuestBadgeId()))
                        .count();
                questBadgeDistinct.setQuestCompletedNumber(questCompleted.intValue());
                questBadgeDistinct.setQuestBadgeId(questBadgeDistinct.getQuestBadgeId());
            }

            result.sort(Comparator.comparing(QuestBadgeResponseDTO::getQuestCompletedNumber).reversed());

            return result;
        }
        return null;
    }

    public Quest findQuestById(Long questId) {
        return questRepository.findQuestByQuestIdAndAndIsDeleted(questId, Boolean.FALSE);
    }

    public QuestStatusResponseDTO updateStatusOfQuest(Quest quest, String status) {
        quest.setStatus(status.toUpperCase());

        Quest questResult = questRepository.save(quest);
        if (questResult != null) {
            QuestStatusResponseDTO result = new QuestStatusResponseDTO();
            result.setQuestId(questResult.getQuestId());
            result.setStatus(questResult.getStatus());
            return result;
        }
        return null;
    }
}
