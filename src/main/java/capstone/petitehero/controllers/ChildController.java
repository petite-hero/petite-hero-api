package capstone.petitehero.controllers;

import capstone.petitehero.dtos.ResponseErrorDTO;
import capstone.petitehero.dtos.ResponseSuccessDTO;
import capstone.petitehero.dtos.request.quest.QuestCreateRequestDTO;
import capstone.petitehero.dtos.request.task.TaskCreateRequestDTO;
import capstone.petitehero.dtos.response.quest.QuestCreateResponseDTO;
import capstone.petitehero.dtos.response.task.TaskCreateResponseDTO;
import capstone.petitehero.entities.Child;
import capstone.petitehero.entities.Parent_Child;
import capstone.petitehero.entities.Quest;
import capstone.petitehero.entities.Task;
import capstone.petitehero.repositories.ParentChildRepository;
import capstone.petitehero.services.ChildService;
import capstone.petitehero.services.QuestService;
import capstone.petitehero.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    private ParentChildRepository parentChildRepository;

    @RequestMapping(value = "/task", method = RequestMethod.POST)
    @ResponseBody
    // parent assign task to their children
    public ResponseEntity<Object> assignTaskByParent(@RequestBody TaskCreateRequestDTO taskCreateRequestDTO) {
        ResponseErrorDTO responseErrorDTO;
        // validate mandatory fields
        if (taskCreateRequestDTO.getName() == null || taskCreateRequestDTO.getName().isEmpty()) {
            responseErrorDTO = new ResponseErrorDTO(400, "Task's name cannot be missing or empty");
            return new ResponseEntity<>(responseErrorDTO, HttpStatus.BAD_REQUEST);
        }
        if (taskCreateRequestDTO.getAssignDate() == null || taskCreateRequestDTO.getAssignDate().isEmpty()) {
            responseErrorDTO = new ResponseErrorDTO(400, "Task's assigned date cannot be missing or empty");
            return new ResponseEntity<>(responseErrorDTO, HttpStatus.BAD_REQUEST);
        }
        if (taskCreateRequestDTO.getDeadline() == null || taskCreateRequestDTO.getDeadline().isEmpty()) {
            responseErrorDTO = new ResponseErrorDTO(400, "Task's deadline cannot be missing or empty");
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
                task.setAssignDate(new Date());
                task.setDeadLine(new Date());
                task.setCreatedDate(new Date());
                task.setIsDeleted(Boolean.FALSE);
                task.setChild(child);
                task.setStatus("CREATED");
                Parent_Child parent_child = parentChildRepository.findParent_ChildByChild_ChildId(childId);
                task.setParent(parent_child.getParent());
                TaskCreateResponseDTO result = taskService.addTaskByParent(task);

                if (result != null) {
                    listData.add(result);
                }

            }
        }
        if (!listData.isEmpty()) {
            ResponseSuccessDTO responseSuccessDTO = new ResponseSuccessDTO(200, "OK", listData);
            return new ResponseEntity<>(responseSuccessDTO, HttpStatus.OK);
        }
        responseErrorDTO = new ResponseErrorDTO(500, "Server is down pls come back again");
        return new ResponseEntity<>(responseErrorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/quest", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> assignQuestByParent(@RequestBody QuestCreateRequestDTO questCreateRequestDTO){
        ResponseErrorDTO responseErrorDTO;

        // validate mandatory fields
        if (questCreateRequestDTO.getName() == null || questCreateRequestDTO.getName().isEmpty()) {
            responseErrorDTO = new ResponseErrorDTO(400,"Quest's name cannot be missing or empty");
            return new ResponseEntity<>(responseErrorDTO, HttpStatus.BAD_REQUEST);
        }

        if (questCreateRequestDTO.getRewardName() == null || questCreateRequestDTO.getRewardName().isEmpty()) {
            responseErrorDTO = new ResponseErrorDTO(400,"Quest's reward name cannot be missing or empty");
            return new ResponseEntity<>(responseErrorDTO, HttpStatus.BAD_REQUEST);
        }

        if (questCreateRequestDTO.getCriteria() == null || questCreateRequestDTO.getCriteria().toString().isEmpty()) {
            responseErrorDTO = new ResponseErrorDTO(400,"Quest's criteria cannot be missing or empty");
            return new ResponseEntity<>(responseErrorDTO, HttpStatus.BAD_REQUEST);
        } else {
            if (!questCreateRequestDTO.getCriteria().toString().matches("\\d+")) {
                responseErrorDTO = new ResponseErrorDTO(400,"Quest's criteria must be a positive number");
                return new ResponseEntity<>(responseErrorDTO, HttpStatus.BAD_REQUEST);
            }
        }

        if (questCreateRequestDTO.getQuestBadge() == null || questCreateRequestDTO.getQuestBadge().isEmpty()) {
            responseErrorDTO = new ResponseErrorDTO(400,"Quest's badge cannot be missing or empty");
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

                quest.setCreatedDate(new Date());
                quest.setProgress(new Integer(0));
                quest.setStatus("IN PROGRESS");

                quest.setChild(child);
                quest.setIsDeleted(Boolean.FALSE);
                Parent_Child parent_child = parentChildRepository.findParent_ChildByChild_ChildId(childId);
                quest.setParent(parent_child.getParent());

                QuestCreateResponseDTO result = questService.addQuestByParent(quest);

                if (result != null) {
                    listData.add(result);
                }
            }
        }
        if (!listData.isEmpty()) {
            ResponseSuccessDTO responseSuccessDTO = new ResponseSuccessDTO(200, "OK", listData);
            return new ResponseEntity<>(responseSuccessDTO, HttpStatus.OK);
        }

        responseErrorDTO = new ResponseErrorDTO(500, "Server is down pls come back again");
        return new ResponseEntity<>(responseErrorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}