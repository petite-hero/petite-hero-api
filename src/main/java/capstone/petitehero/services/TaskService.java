package capstone.petitehero.services;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.ResponseObject;
import capstone.petitehero.dtos.common.*;
import capstone.petitehero.dtos.response.task.*;
import capstone.petitehero.entities.Child;
import capstone.petitehero.entities.Task;
import capstone.petitehero.repositories.TaskRepository;
import capstone.petitehero.utilities.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private NotificationService notiService;

    @Transactional
    public List<TaskCreateResponseDTO> addTaskByParent(List<Task> listTask) {
        List<TaskCreateResponseDTO> result = new ArrayList<>();
        for (Task task : listTask) {
            Task taskResult = taskRepository.save(task);
            if (taskResult != null) {
                // add information of task
                TaskCreateResponseDTO resultData = new TaskCreateResponseDTO();
                resultData.setTaskId(taskResult.getTaskId());
                resultData.setDescription(taskResult.getDescription());

                resultData.setAssignDate(taskResult.getAssignDate());
                resultData.setCreatedDate(taskResult.getCreatedDate());

                resultData.setFromTime(Util.formatTimestampToTime(taskResult.getFromTime().getTime()));
                resultData.setToTime(Util.formatTimestampToTime(taskResult.getToTime().getTime()));

//                resultData.setIsRepeatOn(Util.fromRepeatOnStringToDayInWeek(taskResult.getRepeatOn()));
                resultData.setType(taskResult.getType());
                resultData.setStatus(Constants.status.ASSIGNED.toString());

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
                resultData.setAssigner(assigner);

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
                resultData.setAssignee(assignee);

                if (Util.getStartDay(task.getAssignDate()).longValue()
                        == Util.getStartDay(new Date().getTime()).longValue()) {
                    NotificationDTO notificationDTO = new NotificationDTO();
                    notificationDTO.setAssigner(assigner);
                    notificationDTO.setAssignee(assignee);
                    ArrayList<String> pushTokenList = new ArrayList<>();
                    pushTokenList.add(taskResult.getParent().getPushToken());

                    notiService.pushNotificationMobile(
                            assigner.getFirstName() + assigner.getLastName() + " assigned new task for you" + assignee.getFirstName() + assignee.getLastName()
                            , notificationDTO, pushTokenList);
                }

                result.add(resultData);
            }
        }
        return result;
    }

    public TaskDetailResponseDTO getDetailOfTask(Long taskId) {
        Task taskResult = taskRepository.findTasksByTaskIdAndIsDeleted(taskId, Boolean.FALSE);

        if (taskResult != null) {
            TaskDetailResponseDTO result = new TaskDetailResponseDTO();

            result.setName(taskResult.getName());
            result.setDescription(taskResult.getDescription());

            result.setAssignDate(taskResult.getAssignDate());
            result.setCreatedDate(taskResult.getCreatedDate());

            result.setFromTime(Util.formatTimestampToTime(taskResult.getFromTime().getTime()));
            result.setToTime(Util.formatTimestampToTime(taskResult.getToTime().getTime()));
            result.setType(taskResult.getType());
            result.setStatus(taskResult.getStatus());


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

            if (taskResult.getSubmitDate() != null && !taskResult.getSubmitDate().toString().isEmpty()) {
                result.setSubmitDate(taskResult.getSubmitDate());
            }
            if (taskResult.getProofPhoto() != null && !taskResult.getProofPhoto().isEmpty()) {
                result.setProofPhoto(Util.fromImageFileToBase64String(taskResult.getProofPhoto()));
            }

            return result;
        }

        return null;
    }

    public TaskDeleteResponseDTO deleteTask(Task task) {
        if (task != null) {
            task.setIsDeleted(Boolean.TRUE);
            Task taskDeleted = taskRepository.save(task);

            if (taskDeleted != null) {
                TaskDeleteResponseDTO result = new TaskDeleteResponseDTO();
                result.setTaskId(taskDeleted.getTaskId());
                result.setStatus(Constants.status.DELETED.toString());
                return result;
            }
        }
        return null;
    }

    public List<ListTaskResponseDTO> getChildOfTaskAtAssignedDate(Long childId, Long assignedDateTimeStamp, String provider) {
        List<Task> listTaskResult;
        if (assignedDateTimeStamp != null) {
            Long startDateTimeStamp = Util.getStartDay(assignedDateTimeStamp);
            Long endDateTimeStamp = Util.getEndDay(assignedDateTimeStamp);

            listTaskResult = taskRepository.findTasksByChildChildIdAndIsDeletedAndAssignDateIsBetween(childId, Boolean.FALSE, startDateTimeStamp, endDateTimeStamp);
        } else {
            listTaskResult = taskRepository.findTasksByChildChildIdAndIsDeleted(childId, Boolean.FALSE);
        }

        if (listTaskResult != null) {
            if (!listTaskResult.isEmpty()) {
                List<ListTaskResponseDTO> result = new ArrayList<>();

                for (Task taskResult : listTaskResult) {
                    ListTaskResponseDTO resultData = new ListTaskResponseDTO();

                    resultData.setName(taskResult.getName());
                    resultData.setStatus(taskResult.getStatus());
                    resultData.setTaskId(taskResult.getTaskId());
                    resultData.setFromTime(Util.formatTimestampToTime(taskResult.getFromTime().getTime()));
                    resultData.setToTime(Util.formatTimestampToTime(taskResult.getToTime().getTime()));
                    resultData.setType(taskResult.getType());
                    if (provider != null && provider.equalsIgnoreCase(Constants.SMART_WATCH)) {
                        resultData.setDescription(taskResult.getDescription());
                    }

                    result.add(resultData);
                }
                if (provider != null && provider.equalsIgnoreCase(Constants.SMART_WATCH)) {
                    return result.stream()
                            .filter(task -> task.getStatus().equalsIgnoreCase(Constants.status.ASSIGNED.toString()))
                            .sorted(Comparator.comparing(ListTaskResponseDTO::getFromTime))
                            .collect(Collectors.toList());
                }
                return result.stream()
                        .sorted(Comparator.comparing(ListTaskResponseDTO::getFromTime))
                        .collect(Collectors.toList());
            }
        }
        return null;
    }

    public Task findTaskByTaskId(Long taskId) {
        return taskRepository.findTasksByTaskIdAndIsDeleted(taskId, Boolean.FALSE);
    }

    public TaskUpdateResponseDTO submitTaskForChild(Task task, MultipartFile proofPhoto) {
        task.setProofPhoto(Util.saveImageToSystem(
                task.getTaskId().toString(), "Child_Submitted", proofPhoto));
        task.setStatus(Constants.status.HANDED.toString());

        Task taskResult = taskRepository.save(task);
        if (taskResult != null) {
            TaskUpdateResponseDTO result = new TaskUpdateResponseDTO();

            result.setTaskId(taskResult.getTaskId());

            result.setAssignDate(Util.formatTimestampToDateTime(taskResult.getAssignDate()));
            result.setName(taskResult.getName());

            result.setStatus(Constants.status.HANDED.toString());
            return result;
        }

        return null;
    }

    public TaskUpdateResponseDTO approveTaskFromChild(Task task, Boolean isSuccess) {
        if (isSuccess.booleanValue()) {
            task.setStatus(Constants.status.DONE.toString());
        } else {
            task.setStatus(Constants.status.FAILED.toString());
        }

        Task taskResult = taskRepository.save(task);
        if (taskResult != null) {
            TaskUpdateResponseDTO result = new TaskUpdateResponseDTO();

            result.setTaskId(taskResult.getTaskId());

            result.setAssignDate(Util.formatTimestampToDateTime(taskResult.getAssignDate()));
            result.setName(taskResult.getName());

            if (isSuccess.booleanValue()) {
                result.setStatus(Constants.status.SUCCESS.toString());
            } else {
                result.setStatus(Constants.status.FAILED.toString());
            }
            return result;
        }

        return null;
    }

    public List<ListTaskHandedResponseDTO> getTaskHandedByChildForParent(Long childId, Long dateTimeStamp) {
        Long startDayOfMonth = Util.startDayInMonth(dateTimeStamp);
        Long endDayOfMonth = Util.endDayInMonth(dateTimeStamp);
        List<Task> listTaskResult =
                taskRepository.findTasksByChildChildIdAndAssignDateIsBetweenAndStatusAndIsDeleted(
                        childId, startDayOfMonth, endDayOfMonth,
                        Constants.status.HANDED.toString(), Boolean.FALSE);

        if (listTaskResult != null) {
            List<ListTaskHandedResponseDTO> result = new ArrayList<>();
            if (!listTaskResult.isEmpty()) {
                for (Task task : listTaskResult) {
                    Long startDay = Util.getStartDay(task.getAssignDate());
                    Long endDay = Util.getEndDay(task.getAssignDate());
                    Long count = listTaskResult.stream()
                            .filter(t -> t.getAssignDate() > startDay
                                    && t.getAssignDate() < endDay)
                            .count();
                    ListTaskHandedResponseDTO dataResult = new ListTaskHandedResponseDTO();
                    dataResult.setDate(startDay);
                    dataResult.setCount(count.intValue());

                    result.add(dataResult);
                }
            }
            return result.stream()
                    .filter(Util.distinctByKey(ListTaskHandedResponseDTO::getDate))
                    .collect(Collectors.toList());
        }
        return null;
    }

    public SummaryListTaskResponseDTO summaryChildrenListTask(Long childId) {
        List<Task> listTaskResult = taskRepository.findTasksByChildChildIdAndIsDeleted(childId, Boolean.FALSE);

        if (listTaskResult != null) {
            if (!listTaskResult.isEmpty()) {
                SummaryListTaskResponseDTO result = new SummaryListTaskResponseDTO();
                // get all tasks has type is housework
                List<Task> taskHouseworkAssigned = listTaskResult.stream()
                        .filter(task -> task.getType().equalsIgnoreCase(Constants.taskType.HOUSEWORK.toString()))
                        .collect(Collectors.toList());
                // get all tasks has type is education
                List<Task> taskEducationAssigned = listTaskResult.stream()
                        .filter(task -> task.getType().equalsIgnoreCase(Constants.taskType.EDUCATION.toString()))
                        .collect(Collectors.toList());
                // get all tasks has type is skills
                List<Task> taskSkillsAssigned = listTaskResult.stream()
                        .filter(task -> task.getType().equalsIgnoreCase(Constants.taskType.SKILLS.toString()))
                        .collect(Collectors.toList());

                // summary for task type is housework
                SummaryTaskDetail houseworkTaskType = Util.summaryTaskType(taskHouseworkAssigned);
                // summary for task type is education
                SummaryTaskDetail educationTaskType = Util.summaryTaskType(taskEducationAssigned);
                // summary for task type is skills
                SummaryTaskDetail skillsTaskType = Util.summaryTaskType(taskSkillsAssigned);

                result.setChildId(childId);
                result.setEducationTasks(educationTaskType);
                result.setHouseworkTasks(houseworkTaskType);
                result.setSkillsTasks(skillsTaskType);

                return result;
            }
        }

        return null;
    }

    public Boolean summaryHourOfChildrenTaskList(Long childId, Long dateTimeStamp) {
        Long startDateTimeStamp = Util.getStartDay(dateTimeStamp);
        Long endDateTimeStamp = Util.getEndDay(dateTimeStamp);

        List<Task> taskListResult = taskRepository.findTasksByChildChildIdAndIsDeletedAndAssignDateIsBetween(
                childId, Boolean.FALSE, startDateTimeStamp, endDateTimeStamp);

        if (taskListResult != null) {
            if (!taskListResult.isEmpty()) {
                Boolean isWarning = Boolean.FALSE;
                Long totalHourTaskHasAssigned = 0L;
                for (Task t : taskListResult) {
                    totalHourTaskHasAssigned += t.getToTime().getTime() - t.getFromTime().getTime();
                }
                if (totalHourTaskHasAssigned / (60 * 60 * 1000) >= 3) {
                    isWarning = Boolean.TRUE;
                }
                return isWarning;
            }
        }
        return null;
    }
}
