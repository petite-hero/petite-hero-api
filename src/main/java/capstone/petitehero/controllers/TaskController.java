package capstone.petitehero.controllers;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.ResponseObject;
import capstone.petitehero.dtos.response.task.ListTaskResponseDTO;
import capstone.petitehero.dtos.response.task.TaskDeleteResponseDTO;
import capstone.petitehero.dtos.response.task.TaskDetailResponseDTO;
import capstone.petitehero.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
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
    public ResponseEntity<Object> deleteTaskById(@PathVariable("taskId") Long taskId) {
        ResponseObject responseObject;

        TaskDeleteResponseDTO result = taskService.deleteTask(taskId);
        if (result != null) {
            responseObject = new ResponseObject(200, "OK");
            responseObject.setData(result);
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }

        responseObject = new ResponseObject(404, "Cannot found that task in the system");
        return new ResponseEntity<>(responseObject, HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/list/{childId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getChildListOfTaskByChildId(@PathVariable("childId") Long childId,
                                                     @RequestParam(value = "status", required = false) String status,
                                                     @RequestParam(value = "date", required = false) Long dateTimestamp) {
        ResponseObject responseObject;
        List<ListTaskResponseDTO> listTaskOfChild;
        if (dateTimestamp != null && !dateTimestamp.toString().isEmpty()) {
            if (!dateTimestamp.toString().matches("\\d+")) {
                responseObject = new ResponseObject(400, "Not a right timestamp format");
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
            } else {
                listTaskOfChild = taskService.getChildListOfTask(childId, new Date(dateTimestamp));
            }
        } else {
            listTaskOfChild = taskService.getChildListOfTask(childId, null);
        }
        if (listTaskOfChild != null) {

            List<Object> result = new ArrayList<>();
            for (ListTaskResponseDTO listData : listTaskOfChild) {
                result.add(listData);
            }

            if (listTaskOfChild.isEmpty()) {
                responseObject = new ResponseObject(200, "Child's list of task is empty today");
                responseObject.setData(result);
                return new ResponseEntity<>(responseObject, HttpStatus.OK);
            }

            responseObject = new ResponseObject(200, "OK");
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

        if (proofPhoto != null) {
            System.out.println("Size: " + proofPhoto.getSize());
            System.out.println("SDADAs: " + proofPhoto.getContentType());
        }

        responseObject = new ResponseObject(Constants.CODE_500, "Server is cannot update task for child");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
