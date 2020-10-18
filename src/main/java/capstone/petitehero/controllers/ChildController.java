package capstone.petitehero.controllers;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.ResponseErrorDTO;
import capstone.petitehero.dtos.ResponseObject;
import capstone.petitehero.dtos.ResponseSuccessDTO;
import capstone.petitehero.dtos.request.child.UpdateChildProfileRequestDTO;
import capstone.petitehero.dtos.request.child.VerifyParentRequestDTO;
import capstone.petitehero.dtos.request.quest.QuestCreateRequestDTO;
import capstone.petitehero.dtos.request.task.TaskCreateRequestDTO;
import capstone.petitehero.dtos.response.child.DeleteChildResponseDTO;
import capstone.petitehero.dtos.response.child.UpdateChildProfileResponseDTO;
import capstone.petitehero.dtos.response.child.VerifyParentResponseDTO;
import capstone.petitehero.dtos.response.quest.QuestCreateResponseDTO;
import capstone.petitehero.dtos.response.task.TaskCreateResponseDTO;
import capstone.petitehero.entities.*;
import capstone.petitehero.repositories.AccountRepository;
import capstone.petitehero.services.ChildService;
import capstone.petitehero.services.ParentService;
import capstone.petitehero.services.QuestService;
import capstone.petitehero.services.TaskService;
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
    private AccountRepository accountRepository;

    @RequestMapping(value = "/verify/parent", method = RequestMethod.POST)
    @ResponseBody
    // verify parent from child and system get the smart watch push token
    public ResponseEntity<Object> verifyParentByQRCode(@RequestBody VerifyParentRequestDTO verifyParentRequestDTO) {
        ResponseObject responseObject;
        if (verifyParentRequestDTO.getPushToken() == null || verifyParentRequestDTO.getPushToken().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Device token cannot be missing or empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        if (verifyParentRequestDTO.getToken() == null || verifyParentRequestDTO.getToken().toString().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Token cannot be missing or empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
        if (verifyParentRequestDTO.getChildId() == null || verifyParentRequestDTO.getChildId().toString().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_400, "Child id cannot be missing or empty");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }

        Child child = childService.findChildByChildId(verifyParentRequestDTO.getChildId(), Boolean.FALSE);

        if (verifyParentRequestDTO.getToken().longValue() == child.getCreatedDate()) {

            child.setPushToken(verifyParentRequestDTO.getPushToken());

            VerifyParentResponseDTO result = childService.verifyParentByScanQRCode(child, verifyParentRequestDTO.getParentPhoneNumber());
            if (result != null) {
                responseObject = new ResponseObject(Constants.CODE_200, "Verify successfully. Now parent and children can see each other");
                responseObject.setData(result);
                return new ResponseEntity<>(responseObject, HttpStatus.OK);
            }
            responseObject = new ResponseObject(Constants.CODE_500, "Server is down child cannot verify parent");
            return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            responseObject = new ResponseObject(Constants.CODE_400, "Token is not match");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/{childId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Object> disableChildById(@PathVariable("childId") Long childId) {
        ResponseObject responseObject;

            DeleteChildResponseDTO result = childService.disableChildById(childId);

            if (result != null) {
                responseObject = new ResponseObject(Constants.CODE_200, "OK");
                responseObject.setData(result);
                return new ResponseEntity<>(responseObject, HttpStatus.OK);
            }
        responseObject = new ResponseObject(Constants.CODE_500, "Cannot deleted your children in the system");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/{childId}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<Object> updateChildProfileById(@PathVariable("childId") Long childId,
                                                         @ModelAttribute UpdateChildProfileRequestDTO updateChildProfileRequestDTO,
                                                         @RequestParam(value = "childPhoto", required = false) MultipartFile childPhoto) {
        ResponseObject responseObject;
        Child child = childService.findChildByChildId(childId, Boolean.FALSE);
        if (updateChildProfileRequestDTO.getFirstName() != null && !updateChildProfileRequestDTO.getFirstName().isEmpty()) {
            child.setFirstName(updateChildProfileRequestDTO.getFirstName());
        }
        if (updateChildProfileRequestDTO.getLastName() != null && !updateChildProfileRequestDTO.getLastName().isEmpty()) {
            child.setLastName(updateChildProfileRequestDTO.getLastName());
        }
        if (updateChildProfileRequestDTO.getAge() != null && !updateChildProfileRequestDTO.getAge().toString().isEmpty()) {
            child.setYob(updateChildProfileRequestDTO.getAge());
        }
        if (updateChildProfileRequestDTO.getNickName() != null && !updateChildProfileRequestDTO.getNickName().isEmpty()) {
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
        responseObject = new ResponseObject(500, "Cannot update children profile");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/task", method = RequestMethod.POST)
    @ResponseBody
    // parent assign task to their children
    public ResponseEntity<Object> assignTaskByParent(@RequestBody TaskCreateRequestDTO taskCreateRequestDTO) {
        ResponseErrorDTO responseErrorDTO;
        // validate mandatory fields
        if (taskCreateRequestDTO.getName() == null || taskCreateRequestDTO.getName().isEmpty()) {
            responseErrorDTO = new ResponseErrorDTO(Constants.CODE_400, "Task's name cannot be missing or empty");
            return new ResponseEntity<>(responseErrorDTO, HttpStatus.BAD_REQUEST);
        }
        if (taskCreateRequestDTO.getAssignDate() == null || taskCreateRequestDTO.getAssignDate().isEmpty()) {
            responseErrorDTO = new ResponseErrorDTO(Constants.CODE_400, "Task's assigned date cannot be missing or empty");
            return new ResponseEntity<>(responseErrorDTO, HttpStatus.BAD_REQUEST);
        }
        if (taskCreateRequestDTO.getDeadline() == null || taskCreateRequestDTO.getDeadline().isEmpty()) {
            responseErrorDTO = new ResponseErrorDTO(Constants.CODE_400, "Task's deadline cannot be missing or empty");
            return new ResponseEntity<>(responseErrorDTO, HttpStatus.BAD_REQUEST);
        }
        // TODO validate assign date and deadline in format
        // end validate mandatory fields

        List<Object> listData = new ArrayList<>();
        for (Long childId : taskCreateRequestDTO.getChildId()) {
            Child child = childService.findChildByChildId(childId, Boolean.FALSE);

            if (child != null) {

                Task task = new Task();
                task.setName(taskCreateRequestDTO.getName());
                task.setDescription(taskCreateRequestDTO.getDescription());
                task.setAssignDate(new Date().getTime());
                task.setDeadLine(new Date().getTime());
                task.setCreatedDate(new Date().getTime());
                task.setIsDeleted(Boolean.FALSE);
                task.setChild(child);
                task.setStatus("CREATED");
//                Parent_Child parent_child = parentChildRepository.findParent_ChildByChild_ChildId(childId);
                Account creatorAccount = accountRepository.findAccountByUsername(taskCreateRequestDTO.getCreatorPhoneNumber());

//                task.setParent(creatorAccount.getParentInformation());
                TaskCreateResponseDTO result = taskService.addTaskByParent(task);

                if (result != null) {
                    listData.add(result);
                }

            }
        }
        if (!listData.isEmpty()) {
            ResponseSuccessDTO responseSuccessDTO = new ResponseSuccessDTO(Constants.CODE_200, "OK", listData);
            return new ResponseEntity<>(responseSuccessDTO, HttpStatus.OK);
        }
        responseErrorDTO = new ResponseErrorDTO(Constants.CODE_500, "Server is down pls come back again");
        return new ResponseEntity<>(responseErrorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/quest", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> assignQuestByParent(@RequestBody QuestCreateRequestDTO questCreateRequestDTO){
        ResponseErrorDTO responseErrorDTO;

        // validate mandatory fields
        if (questCreateRequestDTO.getName() == null || questCreateRequestDTO.getName().isEmpty()) {
            responseErrorDTO = new ResponseErrorDTO(Constants.CODE_400,"Quest's name cannot be missing or empty");
            return new ResponseEntity<>(responseErrorDTO, HttpStatus.BAD_REQUEST);
        }

        if (questCreateRequestDTO.getRewardName() == null || questCreateRequestDTO.getRewardName().isEmpty()) {
            responseErrorDTO = new ResponseErrorDTO(Constants.CODE_400,"Quest's reward name cannot be missing or empty");
            return new ResponseEntity<>(responseErrorDTO, HttpStatus.BAD_REQUEST);
        }

        if (questCreateRequestDTO.getCriteria() == null || questCreateRequestDTO.getCriteria().toString().isEmpty()) {
            responseErrorDTO = new ResponseErrorDTO(Constants.CODE_400,"Quest's criteria cannot be missing or empty");
            return new ResponseEntity<>(responseErrorDTO, HttpStatus.BAD_REQUEST);
        } else {
            if (!questCreateRequestDTO.getCriteria().toString().matches("\\d+")) {
                responseErrorDTO = new ResponseErrorDTO(Constants.CODE_400,"Quest's criteria must be a positive number");
                return new ResponseEntity<>(responseErrorDTO, HttpStatus.BAD_REQUEST);
            }
        }

        if (questCreateRequestDTO.getQuestBadge() == null || questCreateRequestDTO.getQuestBadge().isEmpty()) {
            responseErrorDTO = new ResponseErrorDTO(Constants.CODE_400,"Quest's badge cannot be missing or empty");
            return new ResponseEntity<>(responseErrorDTO, HttpStatus.BAD_REQUEST);
        }
        //end validate mandatory fields
        List<Object> listData = new ArrayList<>();
        for (Long childId : questCreateRequestDTO.getChildId()) {
            Child child = childService.findChildByChildId(childId, Boolean.FALSE);

            if (child != null) {
                Quest quest = new Quest();

                quest.setName(questCreateRequestDTO.getName());
                quest.setDescription(questCreateRequestDTO.getDescription());
                quest.setQuestBadge(questCreateRequestDTO.getQuestBadge());
                quest.setRewardName(questCreateRequestDTO.getRewardName());
                quest.setRewardPhoto(questCreateRequestDTO.getRewardPhoto());
                quest.setCriteria(questCreateRequestDTO.getCriteria());

                quest.setCreatedDate(new Date().getTime());
                quest.setProgress(new Integer(0));
                quest.setStatus("IN PROGRESS");

                quest.setChild(child);
                quest.setIsDeleted(Boolean.FALSE);
//                Parent_Child parent_child = parentChildRepository.findParent_ChildByChild_ChildId(childId);
                Account creatorAccount = accountRepository.findAccountByUsername(questCreateRequestDTO.getCreatorPhoneNumber());

//                quest.setParent(creatorAccount.getParentInformation());

                QuestCreateResponseDTO result = questService.addQuestByParent(quest);

                if (result != null) {
                    listData.add(result);
                }
            }
        }
        if (!listData.isEmpty()) {
            ResponseSuccessDTO responseSuccessDTO = new ResponseSuccessDTO(Constants.CODE_200, "OK", listData);
            return new ResponseEntity<>(responseSuccessDTO, HttpStatus.OK);
        }

        responseErrorDTO = new ResponseErrorDTO(Constants.CODE_500, "Server is down pls come back again");
        return new ResponseEntity<>(responseErrorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
