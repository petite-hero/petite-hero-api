package capstone.petitehero.services;

import capstone.petitehero.dtos.common.Assignee;
import capstone.petitehero.dtos.common.Assigner;
import capstone.petitehero.dtos.response.task.TaskCreateResponseDTO;
import capstone.petitehero.entities.Task;
import capstone.petitehero.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
//            result.setDeadline(taskResult.getDeadLine());
//            result.setAssignDate(taskResult.getAssignDate());
            // Todo set created date based on format
//            result.setCreatedDate("");
            result.setStatus("CREATED");

            // information of assigner (colaborator or parent)
            Assigner assigner = new Assigner();
            assigner.setPhoneNumber(task.getParent().getParentPhoneNumber());
            assigner.setFirstName(task.getParent().getFirstName());
            assigner.setLastName(task.getParent().getLastName());
            if (task.getParent().getGender().booleanValue()) {
                assigner.setGender("Male");
            } else {
                assigner.setGender("Female");
            }

            // information of assignee (child)
            Assignee assignee = new Assignee();
            assignee.setChildId(task.getChild().getChildId());
            assignee.setLastName(task.getChild().getLastName());
            assignee.setFirstName(task.getChild().getFirstName());
            assignee.setNickName(task.getChild().getNickName());
            if (task.getChild().getGender().booleanValue()) {
                assignee.setGender("Male");
            } else {
                assignee.setGender("Female");
            }

            return result;
        }
        return null;
    }
}
