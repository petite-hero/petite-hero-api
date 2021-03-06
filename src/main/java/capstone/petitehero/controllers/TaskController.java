package capstone.petitehero.controllers;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.ResponseObject;
import capstone.petitehero.dtos.response.task.*;
import capstone.petitehero.entities.Child;
import capstone.petitehero.entities.Task;
import capstone.petitehero.services.ChildService;
import capstone.petitehero.services.TaskService;
import capstone.petitehero.utilities.Util;
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

    @Autowired
    private ChildService childService;

    @RequestMapping(value = "/{taskId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getDetailOfTaskById(@PathVariable("taskId") Long taskId) {
        ResponseObject responseObject;

        TaskDetailResponseDTO result = taskService.getDetailOfTask(taskId);
        if (result != null) {
            responseObject = new ResponseObject(Constants.CODE_200, "OK");
            responseObject.setData(result);
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }

        responseObject = new ResponseObject(Constants.CODE_404, "Cannot found that task in the system");
        return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/{taskId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Object> deleteTaskById(@PathVariable("taskId") Long taskId) {
        ResponseObject responseObject;

        Task taskResult = taskService.findTaskByTaskId(taskId);
        if (taskResult == null) {
            responseObject = new ResponseObject(Constants.CODE_404, "Cannot found that task in the system");
            return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
        }

        TaskDeleteResponseDTO result = taskService.deleteTask(taskResult);
        if (result != null) {
            responseObject = new ResponseObject(Constants.CODE_200, "OK");
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }

        responseObject = new ResponseObject(Constants.CODE_500, "Cannot delete task for child");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/list/{childId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getChildListOfTaskByChildId(@PathVariable("childId") Long childId,
                                                              @RequestParam(value = "date", required = false) Long dateTimestamp,
                                                              @RequestParam(value = "provider", required = false, defaultValue = Constants.MOBILE) String provider) {
        ResponseObject responseObject;
        List<ListTaskResponseDTO> result;

        Child child = childService.findChildByChildId(childId, Boolean.FALSE);
        if (child == null) {
            responseObject = new ResponseObject(Constants.CODE_404, "Cannot found that child in the system");
            return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
        }

        if (dateTimestamp != null && !dateTimestamp.toString().isEmpty()) {
            result = taskService.getChildOfTaskAtAssignedDate(childId, dateTimestamp, provider);
        } else {
            result = taskService.getChildOfTaskAtAssignedDate(childId, null, provider);
        }

        if (result != null) {
            if (!result.isEmpty()) {
                responseObject = new ResponseObject(Constants.CODE_200, "OK");
            } else {
                responseObject = new ResponseObject(Constants.CODE_200, "List task of child today is empty");
            }
            responseObject.setData(result);
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }
        responseObject = new ResponseObject(Constants.CODE_500, "Cannot get list task of child in the system");
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
        if (proofPhoto != null && !proofPhoto.isEmpty()) {
            if (!Util.validateImageFile(proofPhoto)) {
                responseObject = new ResponseObject(Constants.CODE_400, "That's not an image file");
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
            }
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

        responseObject = new ResponseObject(Constants.CODE_500, "Cannot submit task for child");
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

        responseObject = new ResponseObject(Constants.CODE_500, "Cannot update task for child");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/list/{childId}/handed", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getTaskHandedByChildForParent(@PathVariable("childId") Long childId,
                                                                @RequestParam("date") Long dateTimeStamp) {
        ResponseObject responseObject;

        Child child = childService.findChildByChildId(childId, Boolean.FALSE);
        if (child == null) {
            responseObject = new ResponseObject(Constants.CODE_404, "Cannot found that child in the system");
            return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
        }

        List<ListTaskHandedResponseDTO> result = taskService.getTaskHandedByChildForParent(childId, dateTimeStamp);

        if (result != null) {
            if (result.isEmpty()) {
                responseObject = new ResponseObject(Constants.CODE_200, "Not missing any task handed by child");
                return new ResponseEntity<>(responseObject, HttpStatus.OK);
            } else {
                responseObject = new ResponseObject(Constants.CODE_200, "OK");
            }
            responseObject.setData(result);
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }
        responseObject = new ResponseObject(Constants.CODE_500, "Server cannot get task handed by child");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/{childId}/summary", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> summaryChildrenListTaskForParent(@PathVariable("childId") Long childId,
                                                                   @RequestParam("start") Long startDay,
                                                                   @RequestParam("end") Long endDay) {
        ResponseObject responseObject;

        Child child = childService.findChildByChildId(childId, Boolean.FALSE);
        if (child == null) {
            responseObject = new ResponseObject(Constants.CODE_404, "Cannot found that child in the system");
            return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
        }
        if (startDay == null || startDay.toString().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_404, "Missing start day");
            return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
        } else {
            if (!Util.validateNumber(startDay.toString())) {
                responseObject = new ResponseObject(Constants.CODE_404, "Start day not in right format");
                return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
            }
        }
        if (endDay == null || endDay.toString().isEmpty()) {
            responseObject = new ResponseObject(Constants.CODE_404, "Missing end day");
            return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
        } else {
            if (!Util.validateNumber(endDay.toString())) {
                responseObject = new ResponseObject(Constants.CODE_404, "End day not in right format");
                return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
            }
        }

        SummaryListTaskResponseDTO result = taskService.summaryChildrenListTask(childId, startDay, endDay);
        if (result != null) {
            responseObject = new ResponseObject(Constants.CODE_200, "OK");
            responseObject.setData(result);
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }
        result = new SummaryListTaskResponseDTO();

        responseObject = new ResponseObject(Constants.CODE_200, "Child don't have any task to summarize");
        responseObject.setData(result);
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    @RequestMapping(value = "/{childId}/summary-hour", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> summaryHourChildrenTaskForParent(@PathVariable("childId") Long childId,
                                                                   @RequestParam("date") Long dateTimeStamp,
                                                                   @RequestParam("type") String taskType) {
        ResponseObject responseObject;

        Child child = childService.findChildByChildId(childId, Boolean.FALSE);
        if (child == null) {
            responseObject = new ResponseObject(Constants.CODE_404, "Cannot found that child in the system");
            return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
        }

        Boolean result = taskService.summaryHourOfChildrenTaskList(childId, dateTimeStamp, taskType);
        if (result != null) {
            responseObject = new ResponseObject(Constants.CODE_200, "OK");
            responseObject.setData(result);
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }

        responseObject = new ResponseObject(Constants.CODE_500, "Cannot summary children list of task");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/test/cronjob", method = RequestMethod.GET)
    public void testCronjob(@RequestParam("childId") Long childId) {
        taskService.cronJobTasksTest(childId);
    }
}
