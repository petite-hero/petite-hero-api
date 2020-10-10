package capstone.petitehero.controllers;

import capstone.petitehero.dtos.ResponseErrorDTO;
import capstone.petitehero.dtos.request.task.TaskCreateRequestDTO;
import capstone.petitehero.dtos.response.task.TaskCreateResponseDTO;
import capstone.petitehero.entities.Child;
import capstone.petitehero.entities.Parent_Child;
import capstone.petitehero.entities.Task;
import capstone.petitehero.repositories.ParentChildRepository;
import capstone.petitehero.services.ChildService;
import capstone.petitehero.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/child")
public class ChildController {

    @Autowired
    private ChildService childService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ParentChildRepository parentChildRepository;

    @RequestMapping(value = "/{childId}/tasks", method = RequestMethod.POST)
    @ResponseBody
    // parent assign task to their children
    public ResponseEntity<Object> assignTaskByParent(@PathVariable("childId") Long childId,
                                                     @RequestBody TaskCreateRequestDTO taskCreateRequestDTO) {
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

        Child child = childService.findChildByChildId(childId, Boolean.FALSE);
        if (child != null) {

            Task task = new Task();
            task.setName(taskCreateRequestDTO.getName());
            task.setDescription(taskCreateRequestDTO.getDescription());
//        task.setAssignDate(taskCreateRequestDTO.getAssignDate());
//        task.setDeadLine(taskCreateRequestDTO.getDeadline());
            task.setIsDeleted(Boolean.FALSE);
            task.setChild(child);
            task.setStatus("CREATED");
            Parent_Child parent_child = parentChildRepository.findParent_ChildByChild_ChildId(childId);
            task.setParent(parent_child.getParent());
            TaskCreateResponseDTO result = taskService.addTaskByParent(task);

            if (result != null) {
//                return result;
            }
        } else {
            responseErrorDTO = new ResponseErrorDTO(404, "Cannot find that child in the system");
            return new ResponseEntity<>(responseErrorDTO, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(parentChildRepository.findParent_ChildByChild_ChildId(childId), HttpStatus.OK);
    }
}
