package capstone.petitehero.controllers;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.ResponseObject;
import capstone.petitehero.dtos.request.child.AddChildRequestDTO;
import capstone.petitehero.dtos.request.child.VerifyParentRequestDTO;
import capstone.petitehero.dtos.request.location.PushNotiSWDTO;
import capstone.petitehero.dtos.request.quest.QuestCreateRequestDTO;
import capstone.petitehero.dtos.request.task.TaskCreateRequestDTO;
import capstone.petitehero.dtos.response.child.ChildDetailResponseDTO;
import capstone.petitehero.dtos.response.child.DeleteChildResponseDTO;
import capstone.petitehero.dtos.response.child.UpdateChildProfileResponseDTO;
import capstone.petitehero.dtos.response.child.VerifyParentResponseDTO;
import capstone.petitehero.dtos.response.quest.QuestCreateResponseDTO;
import capstone.petitehero.dtos.response.task.TaskCreateResponseDTO;
import capstone.petitehero.entities.*;
import capstone.petitehero.repositories.ParentRepository;
import capstone.petitehero.services.*;
import capstone.petitehero.utilities.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/child")
public class ChildController {

    @Autowired
    private ChildService childService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private QuestService questService;

    @Autowired
    private ParentService parentService;

    @Autowired
    private ParentChildService parentChildService;

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private NotificationService notiService;

    @RequestMapping(value = "/verify/parent", method = RequestMethod.PUT)
    @ResponseBody
    // verify parent from child and system get the smart watch push token
    public ResponseEntity<Object> verifyParentByQRCode(@RequestBody VerifyParentRequestDTO verifyParentRequestDTO) {
        ResponseObject responseObject;
        if (verifyParentRequestDTO.getChildId() == null || verifyParentRequestDTO.getChildId().toString().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Child id cannot be missing or empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }

        Child child = childService.findChildByChildId(verifyParentRequestDTO.getChildId(), Boolean.FALSE);
        Parent parent = parentRepository.getParentByChildID(verifyParentRequestDTO.getChildId());

        if (child != null) {

            Parent_Child childParent =
                    parentChildService.findChildParentByChildId(verifyParentRequestDTO.getChildId());
//
            if (child.getAndroidId() != null) {
                if (!child.getAndroidId().equals(verifyParentRequestDTO.getAndroidId())) {
                    child.setAndroidId(verifyParentRequestDTO.getAndroidId());
                    child.setDeviceName(verifyParentRequestDTO.getDeviceName());
                    PushNotiSWDTO noti = new PushNotiSWDTO(Constants.SILENT_NOTI, Constants.LOGOUT, new Object());

                    if (child.getPushToken() != null && !child.getPushToken().isEmpty()) {
                        notiService.pushNotificationSW(noti, child.getPushToken());
                    }
                }
            } else {
                child.setAndroidId(verifyParentRequestDTO.getAndroidId());
                child.setDeviceName(verifyParentRequestDTO.getDeviceName());
            }
            child.setPushToken(verifyParentRequestDTO.getPushToken());

            if (childParent != null) {

                VerifyParentResponseDTO result = childService.verifyParentByScanQRCode(child, childParent.getParent().getAccount().getUsername());
                if (result != null) {
                    responseObject = new ResponseObject(Constants.CODE_200, "Verify successfully. Now parent and children can see each other");
                    responseObject.setData(result);

                    ArrayList<String> pushTokens = new ArrayList<>();
                    pushTokens.add(parent.getPushToken());
                    String msg = parent.getLanguage() ? Constants.DONE_SETTING_UP_DEVICE_VN : Constants.DONE_SETTING_UP_DEVICE_EN;
                    notiService.pushNotificationMobile(msg, verifyParentRequestDTO, pushTokens);

                    return new ResponseEntity<>(responseObject, HttpStatus.OK);
                }
                responseObject = new ResponseObject(Constants.CODE_500, "Child cannot verify parent");
                return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                responseObject = new ResponseObject(Constants.CODE_404, "Cannot found parent in the system");
                return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
            }
        } else {
            responseObject = new ResponseObject(Constants.CODE_404, "Cannot found that child by child id");
            return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/{childId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Object> disableChildById(@PathVariable("childId") Long childId,
                                                   @RequestParam(value = "collaborator", required = false) String collaboratorPhoneNumber) {
        ResponseObject responseObject;

        Child child = childService.findChildByChildId(childId, Boolean.FALSE);
        if (child == null) {
            responseObject = new ResponseObject(Constants.CODE_404, "Cannot found that child in the system");
            return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
        }
        DeleteChildResponseDTO result;

        if (collaboratorPhoneNumber != null && !collaboratorPhoneNumber.isEmpty()) {
            Parent collaborator = parentRepository.findParentByAccount_UsernameAndIsDisabled(collaboratorPhoneNumber, Boolean.FALSE);
            if (collaborator == null) {
                responseObject = new ResponseObject(Constants.CODE_404, "Cannot found that collaborator in the system");
                return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
            }
            result = childService.disableChildIdByCollaborator(child, collaborator);
        } else {
            result = childService.disableChildById(child);
        }

        if (result != null) {
            responseObject = new ResponseObject(Constants.CODE_200, "OK");
            responseObject.setData(result);
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }

        responseObject = new ResponseObject(Constants.CODE_500, "Cannot deleted that child in the system");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/{childId}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<Object> updateChildProfileById(@PathVariable("childId") Long childId,
                                                         @ModelAttribute AddChildRequestDTO updateChildProfileRequestDTO,
                                                         @RequestParam(value = "childPhoto", required = false) MultipartFile childPhoto) {
        ResponseObject responseObject;
        Child child = childService.findChildByChildId(childId, Boolean.FALSE);
        if (child == null) {
            responseObject = new ResponseObject(Constants.CODE_404, "Cannot found that child in the system");
            return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
        }
        if (updateChildProfileRequestDTO.getName() != null && !updateChildProfileRequestDTO.getName().isEmpty()) {
            child.setName(updateChildProfileRequestDTO.getName());
        }
        if (updateChildProfileRequestDTO.getYob() != null && !updateChildProfileRequestDTO.getYob().toString().isEmpty()) {
            child.setYob(updateChildProfileRequestDTO.getYob());
        } else {
            if (!Util.checkChildYoB(updateChildProfileRequestDTO.getYob())) {
                responseObject = new ResponseObject(Constants.CODE_400, "Child's must be in 4-11 years old");
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
            }
        }
        if (updateChildProfileRequestDTO.getNickName() != null) {
            child.setNickName(updateChildProfileRequestDTO.getNickName());
        }
        if (updateChildProfileRequestDTO.getGender() != null && !updateChildProfileRequestDTO.getGender().isEmpty()) {
            if (updateChildProfileRequestDTO.getGender().equalsIgnoreCase("Male")) {
                child.setGender(Boolean.TRUE);
            } else {
                child.setGender(Boolean.FALSE);
            }
        }
        if (updateChildProfileRequestDTO.getLanguage() != null && !updateChildProfileRequestDTO.getLanguage().isEmpty()) {
            if (updateChildProfileRequestDTO.getLanguage().equalsIgnoreCase("Vietnamese")) {
                child.setLanguage(Boolean.TRUE);
            } else {
                child.setLanguage(Boolean.FALSE);
            }
        }
        if (childPhoto != null) {
            child.setPhoto(Util.saveImageToSystem(childId.toString(), "Avatar Updated", childPhoto));
        }

        UpdateChildProfileResponseDTO result = childService.updateChildProfile(child);
        if (result != null) {
            responseObject = new ResponseObject(Constants.CODE_200, "OK");
            responseObject.setData(result);
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }
        responseObject = new ResponseObject(Constants.CODE_500, "Cannot update children profile");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/task", method = RequestMethod.POST)
    @ResponseBody
    // parent assign task to their children
    public ResponseEntity<Object> assignTaskByParent(@RequestBody TaskCreateRequestDTO taskCreateRequestDTO) {
        ResponseObject responseObject;
        // validate mandatory fields
        if (taskCreateRequestDTO.getName() == null || taskCreateRequestDTO.getName().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Task's name cannot be missing or empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        if (taskCreateRequestDTO.getDescription() == null || taskCreateRequestDTO.getDescription().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Task's description cannot be missing or empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        if (taskCreateRequestDTO.getFromTime() == null || taskCreateRequestDTO.getFromTime().toString().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Task's start deadline time cannot be missing or empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        if (taskCreateRequestDTO.getToTime() == null || taskCreateRequestDTO.getToTime().toString().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Task's end deadline time cannot be missing or empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        if (!Util.validateFromTimeToTimeOfTask(taskCreateRequestDTO.getFromTime(), taskCreateRequestDTO.getToTime())) {
            responseObject = new ResponseObject(Constants.CODE_400, "Task's end deadline time cannot before task's start deadline");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        if (taskCreateRequestDTO.getType() == null || taskCreateRequestDTO.getType().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Task's type cannot be missing or empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        if (!Util.validateTaskType(taskCreateRequestDTO.getType())) {
            responseObject = new ResponseObject(Constants.CODE_400, "Task's type can only housework, education and skills");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        if (taskCreateRequestDTO.getAssignDateList() == null || taskCreateRequestDTO.getAssignDateList().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Task's assign date cannot be missing or empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        // end validate mandatory fields

        Child child = childService.findChildByChildId(taskCreateRequestDTO.getChildId(), Boolean.FALSE);

        if (child != null) {
            List<Task> taskList = new ArrayList<>();
            Parent creatorInformation = parentService.findParentByPhoneNumber(taskCreateRequestDTO.getCreatorPhoneNumber(), Boolean.FALSE);
            if (creatorInformation == null) {
                responseObject = new ResponseObject(Constants.CODE_404, "Cannot found that creator account in the system");
                return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
            }
            for (Long assignDate : taskCreateRequestDTO.getAssignDateList()) {
                Task task = new Task();
                task.setName(taskCreateRequestDTO.getName());
                task.setDescription(taskCreateRequestDTO.getDescription());
                task.setAssignDate(assignDate);

                task.setFromTime(new Date(Util.setTimeForAssignDate(assignDate, taskCreateRequestDTO.getFromTime())));
                task.setToTime(new Date(Util.setTimeForAssignDate(assignDate, taskCreateRequestDTO.getToTime())));
                task.setCreatedDate(new Date().getTime());
                task.setType(taskCreateRequestDTO.getType());
                task.setIsDeleted(Boolean.FALSE);
                task.setStatus(Constants.status.ASSIGNED.toString());

                task.setChild(child);
                task.setParent(creatorInformation);

                taskList.add(task);
            }

            List<TaskCreateResponseDTO> result = taskService.addTaskByParent(taskList);

            if (!result.isEmpty()) {
                String msg = "Your task on day ";
                int countOverLapTask = 0;
                for (TaskCreateResponseDTO taskResponse : result) {
                    if (taskResponse.getIsOverlap().booleanValue()) {
                        countOverLapTask++;
                        msg += Util.formatTimestampToDate(taskResponse.getAssignDate()) + " ";
                    }
                }
                msg += "overlap";
                if (countOverLapTask == 0) {
                    responseObject = new ResponseObject(Constants.CODE_200, "OK");
                    responseObject.setData(result);
                } else {
                    responseObject = new ResponseObject(Constants.CODE_400, msg);
                    responseObject.setData(result);
                }
                return new ResponseEntity<>(responseObject, HttpStatus.OK);
            }

            responseObject = new ResponseObject(Constants.CODE_500, "Cannot assign task to that child.");
            return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            responseObject = new ResponseObject(Constants.CODE_404, "Cannot found that child in the system");
            return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/quest", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> assignQuestByParent(@ModelAttribute QuestCreateRequestDTO questCreateRequestDTO) {
        ResponseObject responseObject;

        // validate mandatory fields
        if (questCreateRequestDTO.getName() == null || questCreateRequestDTO.getName().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Quest's name cannot be missing or empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
//        if (questCreateRequestDTO.getDescription() == null || questCreateRequestDTO.getDescription().isEmpty()) {
//            responseObject = new ResponseObject(Constants.CODE_400, "Quest's name cannot be missing or empty");
//            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
//        }
        if (questCreateRequestDTO.getReward() == null || questCreateRequestDTO.getReward().toString().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Quest's reward cannot be missing or empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
//        if (questCreateRequestDTO.getTitle() == null || questCreateRequestDTO.getTitle().isEmpty()) {
//            responseObject = new ResponseObject(Constants.CODE_400, "Quest's title cannot be missing or empty");
//            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
//        }
        //end validate mandatory fields
        Child child = childService.findChildByChildId(questCreateRequestDTO.getChildId(), Boolean.FALSE);

        if (child != null) {
            Parent creatorInformation = parentService.findParentByPhoneNumber(questCreateRequestDTO.getCreatorPhoneNumber(), Boolean.FALSE);
            if (creatorInformation == null) {
                responseObject = new ResponseObject(Constants.CODE_404, "Cannot found that creator account in the system");
                return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
            }
            Quest quest = new Quest();

            quest.setName(questCreateRequestDTO.getName());
            quest.setDescription(questCreateRequestDTO.getDescription());
            quest.setReward(questCreateRequestDTO.getReward());
            quest.setIsDeleted(Boolean.FALSE);
            quest.setTitle(questCreateRequestDTO.getTitle());
            quest.setCreatedDate(new Date().getTime());
            quest.setStatus(Constants.status.ASSIGNED.toString());

            quest.setChild(child);
            quest.setParent(creatorInformation);

            QuestCreateResponseDTO result = questService.addQuestByParentOrCollaborator(quest);

            if (result != null) {
                responseObject = new ResponseObject(Constants.CODE_200, "OK");
                responseObject.setData(result);
                return new ResponseEntity<>(responseObject, HttpStatus.OK);
            } else {
                responseObject = new ResponseObject(Constants.CODE_500, "Server is down pls come back again");
                return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            responseObject = new ResponseObject(Constants.CODE_404, "Cannot assign quest to that child because cannot found that child in the system ");
            return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/{childId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getChildDetail(@PathVariable("childId") Long childId) {
        ResponseObject responseObject;

        ChildDetailResponseDTO result = childService.getDetailOfChild(childId);
        if (result != null) {
            responseObject = new ResponseObject(Constants.CODE_200, "OK");
            responseObject.setData(result);
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }

        responseObject = new ResponseObject(Constants.CODE_404, "Cannot found that child in the system");
        return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/tracking/{child}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseObject getSWTrackingStatus(@PathVariable(value = "child") Long child) {
        return childService.getSWTrackingStatus(child);
    }

    @RequestMapping(value = "/{childId}/delete-device", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Object> deleteChildDevice(@PathVariable("childId") Long childId) {
        ResponseObject responseObject;
        Child child = childService.findChildByChildId(childId, Boolean.FALSE);
        if (child == null) {
            responseObject = new ResponseObject(Constants.CODE_404, "Cannot found that child in the system");
            return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
        }

        DeleteChildResponseDTO result = childService.resetChildDevice(child);
        if (result != null) {
            responseObject = new ResponseObject(Constants.CODE_200, "OK");
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }

        responseObject = new ResponseObject(Constants.CODE_500, "Cannot reset child device in the system");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
