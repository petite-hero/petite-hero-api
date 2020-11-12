package capstone.petitehero.services;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.common.Assignee;
import capstone.petitehero.dtos.common.Assigner;
import capstone.petitehero.dtos.common.NotificationDTO;
import capstone.petitehero.dtos.request.location.PushNotiSWDTO;
import capstone.petitehero.dtos.response.quest.*;
import capstone.petitehero.dtos.response.quest.badge.QuestBadgeResponseDTO;
import capstone.petitehero.entities.Parent_Child;
import capstone.petitehero.entities.Quest;
import capstone.petitehero.repositories.QuestRepository;
import capstone.petitehero.utilities.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestService {

    @Autowired
    private QuestRepository questRepository;

    @Autowired
    private NotificationService notiService;

    public QuestCreateResponseDTO addQuestByParentOrCollaborator(Quest quest, MultipartFile rewardPhoto) {
        Quest questResult = questRepository.save(quest);

        if (questResult != null) {
            QuestCreateResponseDTO result = new QuestCreateResponseDTO();

            result.setQuestId(questResult.getQuestId());
            result.setName(questResult.getName());
            result.setDescription(questResult.getDescription());
            result.setReward(questResult.getReward());

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

//            // send notification when a quest is created to parent's mobile
//            // and child' smart watch
//            if (!questResult.getChild().getChild_parentCollection()
//                    .stream()
//                    .anyMatch(pc ->
//                            pc.getParent().getId().longValue() ==
//                                    questResult.getParent().getId().longValue())) {
//
//                // send noti to parent's mobile when a collaborator create task to their children.
//                NotificationDTO notificationDTO = new NotificationDTO();
//                notificationDTO.setData(questResult);
//                ArrayList<String> pushTokenList = new ArrayList<>();
//                pushTokenList.add(questResult.getChild().getChild_parentCollection()
//                        .stream()
//                        .findFirst().orElse(null)
//                        .getParent().getPushToken());
//
//                notiService.pushNotificationMobile(
//                        assigner.getFirstName() + " " + assigner.getLastName() +
//                                " assigned new quest to "
//                                + assignee.getFirstName() + " " + assignee.getLastName()
//                        , notificationDTO, pushTokenList);
//
//            }

            // send silent noty to children's smart watch
            if (questResult.getChild().getPushToken() != null && !questResult.getChild().getPushToken().isEmpty()) {
                PushNotiSWDTO noty = new PushNotiSWDTO(Constants.PETITE_HERO,
                        questResult.getParent().getFirstName() + " " + questResult.getParent().getLastName() +
                                " đã tạo nhiệm vụ mới cho con"
                        , result);
                notiService.pushNotificationSW(noty, questResult.getChild().getPushToken());
            }

            return result;
        }
        return null;
    }


    public QuestDetailResponseDTO getDetailOfQuest(Long questId) {
        Quest questResult = questRepository.findQuestByQuestIdAndAndIsDeleted(questId, Boolean.FALSE);

        if (questResult != null) {
            QuestDetailResponseDTO result = new QuestDetailResponseDTO();
            result.setQuestId(questResult.getQuestId());
            result.setName(questResult.getName());
            result.setCreatedDate(questResult.getCreatedDate());
            result.setStatus(questResult.getStatus());
            result.setReward(questResult.getReward());
            result.setDescription(questResult.getDescription());

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

                if (questResult.getChild().getPushToken() != null && !questResult.getChild().getPushToken().isEmpty()) {
                    PushNotiSWDTO noty = new PushNotiSWDTO(Constants.SILENT_NOTI, Constants.UPDATED_QUEST, result);
                    notiService.pushNotificationSW(noty, questResult.getChild().getPushToken());
                }
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
            if (!listQuestResult.isEmpty()) {
                listQuestResult.sort(Comparator.comparing(Quest::getCreatedDate).reversed());
                for (Quest questResult : listQuestResult) {
                    ListQuestResponseDTO resultData = new ListQuestResponseDTO();

                    resultData.setQuestId(questResult.getQuestId());
                    resultData.setName(questResult.getName());
                    resultData.setStatus(questResult.getStatus());
                    resultData.setDescription(questResult.getDescription());
                    System.out.println("DATE: " + Util.formatTimestampToDateTime(questResult.getCreatedDate()));

                    if (questResult.getReward() != null) {
                        resultData.setReward(questResult.getReward());
                    }

                    result.add(resultData);
                }
            }
            return result;
        }
        return null;
    }


    public List<QuestBadgeResponseDTO> getBadgeListChildArchived(Long childId) {
        List<Quest> listQuestResult =
                questRepository.findQuestsByChildChildIdAndIsDeletedAndStatus(
                        childId, Boolean.FALSE, Constants.status.DONE.toString());

        if (listQuestResult != null) {
            List<QuestBadgeResponseDTO> result = new ArrayList<>();
            if (!listQuestResult.isEmpty()) {
                List<Quest> filterQuestBadgeList = listQuestResult.stream()
                        .filter(Util.distinctByKey(Quest::getReward)).collect(Collectors.toList());

                for (Quest questBadgeDistinct : filterQuestBadgeList) {
                    QuestBadgeResponseDTO dataResult = new QuestBadgeResponseDTO();
                    dataResult.setQuestBadgeId(questBadgeDistinct.getReward());

                    result.add(dataResult);
                }

                for (QuestBadgeResponseDTO questBadgeDistinct : result) {
                    Long questCompleted = listQuestResult.stream()
                            .filter(quest -> quest.getReward().equals(questBadgeDistinct.getQuestBadgeId()))
                            .count();
                    questBadgeDistinct.setQuestCompletedNumber(questCompleted.intValue());
                    questBadgeDistinct.setQuestBadgeId(questBadgeDistinct.getQuestBadgeId());
                }

                result.sort(Comparator.comparing(QuestBadgeResponseDTO::getQuestCompletedNumber).reversed());
            }
            return result;
        }
        return null;
    }

    public List<ListQuestResponseDTO> getListBadgesChildArchivedSmartWatch(Long childId, Integer maxBadges) {
        List<Quest> listQuestResult = questRepository.findTopQuestsByChild_ChildIdAndIsDeletedAndStatus(
                childId, Constants.status.DONE.toString(), Boolean.FALSE, maxBadges
        );

        if (listQuestResult != null) {
            List<ListQuestResponseDTO> result = new ArrayList<>();
            if (!listQuestResult.isEmpty()) {
                for (Quest quest : listQuestResult) {
                    ListQuestResponseDTO resultData = new ListQuestResponseDTO();

                    resultData.setQuestId(quest.getQuestId());
                    resultData.setName(quest.getName());
                    resultData.setDescription(quest.getDescription());
                    resultData.setReward(quest.getReward());
                    resultData.setStatus(quest.getStatus());

                    result.add(resultData);
                }
            }
            return result;
        }
        return null;
    }

    public Quest findQuestById(Long questId) {
        return questRepository.findQuestByQuestIdAndAndIsDeleted(questId, Boolean.FALSE);
    }

    public QuestStatusResponseDTO updateStatusOfQuest(Quest quest, Boolean isSuccess) {
        if (isSuccess.booleanValue()) {
            quest.setStatus(Constants.status.DONE.toString());
        } else {
            quest.setStatus(Constants.status.FAILED.toString());
        }
        quest.setSubmitDate(new Date().getTime());

        Quest questResult = questRepository.save(quest);
        if (questResult != null) {
            QuestStatusResponseDTO result = new QuestStatusResponseDTO();
            result.setQuestId(questResult.getQuestId());
            result.setStatus(questResult.getStatus());

            if (questResult.getChild().getPushToken() != null && !questResult.getChild().getPushToken().isEmpty()) {
                String msg = "";
                if (questResult.getStatus().equalsIgnoreCase(Constants.status.DONE.toString())) {
                    msg = questResult.getName() + " đã thành công";
                }
                PushNotiSWDTO noty = new PushNotiSWDTO(Constants.PETITE_HERO, msg, result);
                notiService.pushNotificationSW(noty, questResult.getChild().getPushToken());
            }
            return result;
        }
        return null;
    }
}
