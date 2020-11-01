package capstone.petitehero.controllers;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.ResponseObject;
import capstone.petitehero.dtos.response.task.*;
import capstone.petitehero.entities.Task;
import capstone.petitehero.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @RequestMapping(value = "/{taskId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getDetailOfTaskById(@PathVariable("taskId") Long taskId) {
        ResponseObject responseObject;

        TaskDetailResponseDTO result = taskService.getDetailOfTask(taskId);
        if (result != null) {
            responseObject = new ResponseObject(200, "OK");
            responseObject.setData(result);
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }

        responseObject = new ResponseObject(404, "Cannot found that task in the system");
        return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/{taskId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Object> deleteTaskById(@PathVariable("taskId") Long taskId,
                                                 @RequestParam(value = "duplicated", required = false) Boolean isDuplicatedTask) {
        ResponseObject responseObject;

        Task taskResult = taskService.findTaskByTaskId(taskId);
        if (taskResult == null) {
            responseObject = new ResponseObject(Constants.CODE_404, "Cannot found that task in the system");
            return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
        }

        if (isDuplicatedTask != null) {
            if (isDuplicatedTask.booleanValue()) {
                TaskDeleteResponseDTO result = taskService.deleteTask(taskResult, Boolean.TRUE);
                if (result != null) {
                    responseObject = new ResponseObject(200, "OK");
                    responseObject.setData(result);
                    return new ResponseEntity<>(responseObject, HttpStatus.OK);
                }

                responseObject = new ResponseObject(Constants.CODE_500, "Server cannot delete task for child");
                return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        TaskDeleteResponseDTO result = taskService.deleteTask(taskResult, null);
        if (result != null) {
            responseObject = new ResponseObject(200, "Task and all duplicated task from this is all deleted");
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }

        responseObject = new ResponseObject(Constants.CODE_500, "Server cannot delete task for child");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/list/{childId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getChildListOfTaskByChildId(@PathVariable("childId") Long childId,
                                                     @RequestParam(value = "date", required = false) Long dateTimestamp) {
        ResponseObject responseObject;
        List<Task> listTaskOfChild;
        if (dateTimestamp != null && !dateTimestamp.toString().isEmpty()) {
            if (!dateTimestamp.toString().matches("\\d+")) {
                responseObject = new ResponseObject(400, "Not a right timestamp format");
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
            } else {
                listTaskOfChild = taskService.getChildOfTaskAtAssignedDate(childId, dateTimestamp);
            }
        } else {
            listTaskOfChild = taskService.getChildOfTaskAtAssignedDate(childId, null);
        }
        if (listTaskOfChild != null) {
            // duplicated task has repeat on
            List<Task> taskDuplicated = taskService.findAllChildTaskHasRepeatOn(childId, dateTimestamp);
            if (!taskDuplicated.isEmpty()) {
                for (Task task :  taskDuplicated) {
                    listTaskOfChild.add(task);
                }
            }

            List<ListTaskResponseDTO> result = taskService.getChildListOfTask(listTaskOfChild);

            if (!result.isEmpty()) {
                responseObject = new ResponseObject(Constants.CODE_200, "OK");
            } else {
                responseObject = new ResponseObject(Constants.CODE_200, "List task of child today is empty");
            }
            responseObject.setData(result);
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }

        responseObject = new ResponseObject(500, "Server is down cannot get children list of task");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/{taskId}/submit", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<Object> submitTaskFromChild(@PathVariable("taskId") Long taskId,
                                                      @RequestParam("proof") MultipartFile proofPhoto) {
        ResponseObject responseObject;

        if (proofPhoto == null) {
            responseObject = new ResponseObject(Constants.CODE_400, "You must have proof photo to submit task");
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }

        Task taskResult = taskService.findTaskByTaskId(taskId);
        if (taskResult == null) {
            responseObject = new ResponseObject(Constants.CODE_404, "Cannot found that task in the system");
            return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
        }

        TaskUpdateResponseDTO result = taskService.submitTaskForChild(taskResult, proofPhoto);
        if (result != null) {
            responseObject = new ResponseObject(Constants.CODE_200, "OK");
            responseObject.setData(result);
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }

        responseObject = new ResponseObject(Constants.CODE_500, "Server cannot update task for child");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/{taskId}/approve", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<Object> approveTaskFromChild(@PathVariable("taskId") Long taskId,
                                                      @RequestParam("success") Boolean isSuccess) {
        ResponseObject responseObject;

        Task taskResult = taskService.findTaskByTaskId(taskId);
        if (taskResult == null) {
            responseObject = new ResponseObject(Constants.CODE_404, "Cannot found that task in the system");
            return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
        }

        TaskUpdateResponseDTO result = taskService.approveTaskFromChild(taskResult, isSuccess);
        if (result != null) {
            responseObject = new ResponseObject(Constants.CODE_200, "OK");
            responseObject.setData(result);
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }

        responseObject = new ResponseObject(Constants.CODE_500, "Server cannot update task for child");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/list/{childId}/handed", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getTaskHandedByChildForParent(@PathVariable("childId") Long childId,
                                                                @RequestParam("date") Long dateTimeStamp) {
        ResponseObject responseObject;

        List<ListTaskHandedResponseDTO> result = taskService.getTaskHandedByChildForParent(childId, dateTimeStamp);

        if (result != null) {
            if (result.isEmpty()) {
                responseObject = new ResponseObject(Constants.CODE_200, "Not missing any task handed by child");
                responseObject.setData(result);
                return new ResponseEntity<>(responseObject, HttpStatus.OK);
            }
            responseObject = new ResponseObject(Constants.CODE_200, "OK");
            responseObject.setData(result);
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }

        responseObject = new ResponseObject(500, "Server is down cannot get children list of task");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
