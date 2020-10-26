package capstone.petitehero.services;

import capstone.petitehero.dtos.common.Assignee;
import capstone.petitehero.dtos.common.Assigner;
import capstone.petitehero.dtos.response.task.ListTaskResponseDTO;
import capstone.petitehero.dtos.response.task.TaskCreateResponseDTO;
import capstone.petitehero.dtos.response.task.TaskDeleteResponseDTO;
import capstone.petitehero.dtos.response.task.TaskDetailResponseDTO;
import capstone.petitehero.entities.Task;
import capstone.petitehero.repositories.TaskRepository;
import capstone.petitehero.utilities.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public TaskCreateResponseDTO addTaskByParent(Task task) {
        Task taskResult = taskRepository.save(task);
        if (taskResult != null) {
            // add information of task
            TaskCreateResponseDTO result = new TaskCreateResponseDTO();
            result.setTaskId(taskResult.getTaskId());
            result.setDescription(taskResult.getDescription());
            result.setDeadline(Util.formatDateTime(taskResult.getDeadLine()));
            result.setAssignDate(Util.formatDateTime(taskResult.getAssignDate()));
            // Todo set created date based on format
            result.setCreatedDate(Util.formatDateTime(taskResult.getCreatedDate()));
            result.setStatus("CREATED");

            // information of assigner (colaborator or parent)
            Assigner assigner = new Assigner();
            assigner.setPhoneNumber(taskResult.getParent().getAccount().getUsername());
            assigner.setFirstName(taskResult.getParent().getFirstName());
            assigner.setLastName(taskResult.getParent().getLastName());
            if (taskResult.getParent().getGender().booleanValue()) {
                assigner.setGender("Male");
            } else {
                assigner.setGender("Female");
            }
            result.setAssigner(assigner);

            // information of assignee (child)
            Assignee assignee = new Assignee();
            assignee.setChildId(taskResult.getChild().getChildId());
            assignee.setLastName(taskResult.getChild().getLastName());
            assignee.setFirstName(taskResult.getChild().getFirstName());
            assignee.setNickName(taskResult.getChild().getNickName());
            if (taskResult.getChild().getGender().booleanValue()) {
                assignee.setGender("Male");
            } else {
                assignee.setGender("Female");
            }
            result.setAssignee(assignee);

            return result;
        }
        return null;
    }

    public TaskDetailResponseDTO getDetailOfTask(Long taskId) {
        Task taskResult = taskRepository.findTasksByTaskIdAndIsDeleted(taskId, Boolean.FALSE);

        if (taskResult != null) {
            TaskDetailResponseDTO result = new TaskDetailResponseDTO();

            result.setName(taskResult.getName());
            result.setDescription(taskResult.getDescription());

            result.setDeadline(Util.formatDateTime(taskResult.getDeadLine()));
            result.setAssignDate(Util.formatDateTime(taskResult.getAssignDate()));
            result.setCreatedDate(Util.formatDateTime(taskResult.getCreatedDate()));

            // information of assigner (colaborator or parent)
            Assigner assigner = new Assigner();
            assigner.setPhoneNumber(taskResult.getParent().getAccount().getUsername());
            assigner.setFirstName(taskResult.getParent().getFirstName());
            assigner.setLastName(taskResult.getParent().getLastName());
            if (taskResult.getParent().getGender().booleanValue()) {
                assigner.setGender("Male");
            } else {
                assigner.setGender("Female");
            }
            result.setAssigner(assigner);

            // information of assignee (child)
            Assignee assignee = new Assignee();
            assignee.setChildId(taskResult.getChild().getChildId());
            assignee.setLastName(taskResult.getChild().getLastName());
            assignee.setFirstName(taskResult.getChild().getFirstName());
            assignee.setNickName(taskResult.getChild().getNickName());
            if (taskResult.getChild().getGender().booleanValue()) {
                assignee.setGender("Male");
            } else {
                assignee.setGender("Female");
            }
            result.setAssignee(assignee);

            // TODO
            result.setSubmitDate("string");
            result.setProofPhoto("string");

            return result;
        }

        return null;
    }

    public TaskDeleteResponseDTO deleteTask(Long taskId) {
        Task task = taskRepository.findTaskByTaskId(taskId);

        task.setIsDeleted(Boolean.TRUE);
        if (task != null) {
            Task taskDeleted = taskRepository.save(task);

            if (taskDeleted != null) {
                TaskDeleteResponseDTO result = new TaskDeleteResponseDTO();
                result.setTaskId(taskDeleted.getTaskId());
                result.setStatus("DELETED");
                return result;
            }
        }
        return null;
    }
    
    public List<ListTaskResponseDTO> getChildListOfTask(Long childId, Date date) {
        List<Task> listTaskResult;
        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.AM_PM, Calendar.AM);


            // set hour, minutes, seconds, milliseconds at start date
            calendar.set(Calendar.HOUR, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Long startDateTimeStamp = calendar.getTimeInMillis();

            // set hour, minutes, seconds, milliseconds at end date
            calendar.set(Calendar.HOUR, 11);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            Long endDateTimeStamp = calendar.getTimeInMillis();

            listTaskResult = taskRepository.findTasksByChildChildIdAndIsDeletedAndAssignDateIsBetween(childId, Boolean.FALSE, startDateTimeStamp, endDateTimeStamp);
        } else {
            listTaskResult = taskRepository.findTasksByChildChildIdAndIsDeleted(childId, Boolean.FALSE);
        }

        if (listTaskResult != null) {
            List<ListTaskResponseDTO> result = new ArrayList<>();
            for (Task taskResult: listTaskResult) {
                ListTaskResponseDTO resultData = new ListTaskResponseDTO();

                resultData.setName(taskResult.getName());
                resultData.setStatus(taskResult.getStatus());
                resultData.setTaskId(taskResult.getTaskId());
                resultData.setDeadline(Util.formatDateTime(taskResult.getDeadLine()));

                result.add(resultData);
            }
            return result;
        }
        
        return null;
    } 
}
