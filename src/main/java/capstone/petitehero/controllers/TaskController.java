package capstone.petitehero.controllers;

import capstone.petitehero.dtos.ResponseErrorDTO;
import capstone.petitehero.dtos.ResponseSuccessDTO;
import capstone.petitehero.dtos.response.task.ListTaskResponseDTO;
import capstone.petitehero.dtos.response.task.TaskDeleteResponseDTO;
import capstone.petitehero.dtos.response.task.TaskDetailResponseDTO;
import capstone.petitehero.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
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
        ResponseErrorDTO responseErrorDTO;

        TaskDetailResponseDTO result = taskService.getDetailOfTask(taskId);
        if (result != null) {
            List<Object> listData = new ArrayList<>();
            listData.add(result);
            ResponseSuccessDTO responseSuccessDTO = new ResponseSuccessDTO(200, "OK", listData);
            return new ResponseEntity<>(responseSuccessDTO, HttpStatus.OK);
        }

        responseErrorDTO = new ResponseErrorDTO(404, "Cannot found that task in the system");
        return new ResponseEntity<>(responseErrorDTO, HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/{taskId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Object> deleteTaskById(@PathVariable("taskId") Long taskId) {
        ResponseErrorDTO responseErrorDTO;

        TaskDeleteResponseDTO result = taskService.deleteTask(taskId);
        if (result != null) {
            List<Object> listData = new ArrayList<>();
            listData.add(result);
            ResponseSuccessDTO responseSuccessDTO = new ResponseSuccessDTO(200, "OK", listData);
            return new ResponseEntity<>(responseSuccessDTO, HttpStatus.OK);
        }

        responseErrorDTO = new ResponseErrorDTO(404, "Cannot found that task in the system");
        return new ResponseEntity<>(responseErrorDTO, HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/list/{childId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getChildListOfTaskByChildId(@PathVariable("childId") Long childId,
                                                     @RequestParam(value = "date", required = false) Long dateTimestamp) {
        ResponseErrorDTO responseErrorDTO;
        List<ListTaskResponseDTO> listTaskOfChild;
        if (dateTimestamp != null && !dateTimestamp.toString().isEmpty()) {
            if (!dateTimestamp.toString().matches("\\d+")) {
                responseErrorDTO = new ResponseErrorDTO(400, "Not a right timestamp format");
                return new ResponseEntity<>(responseErrorDTO, HttpStatus.BAD_REQUEST);
            } else {
                listTaskOfChild = taskService.getChildListOfTask(childId, new Date(dateTimestamp));
            }
        } else {
            listTaskOfChild = taskService.getChildListOfTask(childId, null);
        }
        if (listTaskOfChild != null) {
            ResponseSuccessDTO responseSuccessDTO;

            List<Object> result = new ArrayList<>();
            for (ListTaskResponseDTO listData : listTaskOfChild) {
                result.add(listData);
            }

            if (listTaskOfChild.isEmpty()) {
                responseSuccessDTO = new ResponseSuccessDTO(200, "Child's list of task is empty today", result);
                return new ResponseEntity<>(responseSuccessDTO, HttpStatus.OK);
            }

            responseSuccessDTO = new ResponseSuccessDTO(200, "OK", result);
            return new ResponseEntity<>(responseSuccessDTO, HttpStatus.OK);
        }

        responseErrorDTO = new ResponseErrorDTO(500, "Server is down cannot get children list of task");
        return new ResponseEntity<>(responseErrorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
