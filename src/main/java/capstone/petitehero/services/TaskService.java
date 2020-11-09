package capstone.petitehero.services;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.common.Assignee;
import capstone.petitehero.dtos.common.Assigner;
import capstone.petitehero.dtos.common.ChildInformation;
import capstone.petitehero.dtos.common.NotificationDTO;
import capstone.petitehero.dtos.response.task.*;
import capstone.petitehero.entities.Child;
import capstone.petitehero.entities.Task;
import capstone.petitehero.repositories.TaskRepository;
import capstone.petitehero.utilities.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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

//            if (taskResult.getIsDuplicateTask() != null) {
//                Task tempTask = taskRepository.findTasksByTaskIdAndIsDeleted(
//                        taskResult.getIsDuplicateTask(), Boolean.FALSE);
//                result.setIsRepeatOn(Util.fromRepeatOnStringToDayInWeek(tempTask.getRepeatOn()));
//            } else {
//                result.setIsRepeatOn(Util.fromRepeatOnStringToDayInWeek(taskResult.getRepeatOn()));
//            }

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

    public TaskDeleteResponseDTO deleteTask(Task task, Boolean isDuplicatedTask) {
//        if (isDuplicatedTask != null) {
//            if (isDuplicatedTask.booleanValue()) {
//                if (task != null) {
//                    task.setIsDeleted(Boolean.TRUE);
//                    Task taskDeleted = taskRepository.save(task);
//
//                    if (taskDeleted != null) {
//                        TaskDeleteResponseDTO result = new TaskDeleteResponseDTO();
//                        result.setTaskId(taskDeleted.getTaskId());
//                        result.setStatus("DELETED");
//                        return result;
//                    }
//                }
//                return null;
//            }
//        }
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
    
    public List<Task> getChildOfTaskAtAssignedDate(Long childId, Long assignedDateTimeStamp) {
        List<Task> listTaskResult;
        if (assignedDateTimeStamp != null) {
            Long startDateTimeStamp = Util.getStartDay(assignedDateTimeStamp);
            Long endDateTimeStamp = Util.getEndDay(assignedDateTimeStamp);

            listTaskResult = taskRepository.findTasksByChildChildIdAndIsDeletedAndAssignDateIsBetween(childId, Boolean.FALSE, startDateTimeStamp, endDateTimeStamp);
        } else {
            listTaskResult = taskRepository.findTasksByChildChildIdAndIsDeleted(childId, Boolean.FALSE);
        }

        if (listTaskResult != null) {
            return listTaskResult;
        }

        return null;
    }

    public List<Task> findAllChildTaskHasRepeatOn(Long childId, Long timeStampDateRepeat) {
        List<Task> result = new ArrayList<>();
        List<Task> allTaskHasRepeat = taskRepository.findTasksByChildChildIdAndIsDeletedAndRepeatOnIsNotNull(childId, Boolean.FALSE);

        // filter task that's already duplicated
        Long startDateTimeStamp = Util.getStartDay(timeStampDateRepeat);
        Long endDateTimeStamp = Util.getEndDay(timeStampDateRepeat);
        List<Task> taskHasBeenDuplicated = taskRepository.findTasksByChildChildIdAndAssignDateIsBetweenAndAndIsDuplicateTaskIsNotNull(
            childId, startDateTimeStamp, endDateTimeStamp);

        // find all the task that need duplicate
        int indexOfDayRepeat = Util.fromTimeStampToDayInWeek(timeStampDateRepeat);
        List<Task> taskNeedToRepeat = new ArrayList<>();
        for (Task task : allTaskHasRepeat) {
            // check the string repeat on of task at index of n is 1
            // if is 1 that's the task need to duplicate in the system
            if (String.format("%c", task.getRepeatOn().charAt(indexOfDayRepeat)).equals("1")
                    && task.getCreatedDate() < Util.getStartDay(timeStampDateRepeat)
                    && Util.getStartDay(task.getAssignDate()).longValue() != Util.getStartDay(timeStampDateRepeat)) {
                taskNeedToRepeat.add(task);
            }
        }
        // end find all the task that need duplicate

        // filter all task that all ready duplicated
        if (!taskHasBeenDuplicated.isEmpty()) {
            for (Task task : taskHasBeenDuplicated) {
                Task alreadyDuplicated = taskNeedToRepeat.stream()
                        .filter(t -> t.getTaskId() == task.getIsDuplicateTask())
                        .findAny().orElse(null);
                if (alreadyDuplicated != null) {
                    taskNeedToRepeat.remove(alreadyDuplicated);
                }
            }
        }
        // end filter all task that all ready duplicated

        // duplicate task and save to the system
        if (!taskNeedToRepeat.isEmpty()) {
            for (Task taskNeedDuplicate : taskNeedToRepeat) {
                Task taskDuplicate = new Task();

                // basic information that's not change
                taskDuplicate.setName(taskNeedDuplicate.getName());
                taskDuplicate.setDescription(taskNeedDuplicate.getDescription());
                taskDuplicate.setParent(taskNeedDuplicate.getParent());
                taskDuplicate.setChild(taskNeedDuplicate.getChild());
                taskDuplicate.setCreatedDate(taskNeedDuplicate.getCreatedDate());
                taskDuplicate.setIsDeleted(Boolean.FALSE);
                taskDuplicate.setType(taskNeedDuplicate.getType());

                // information that's change
                taskDuplicate.setAssignDate(timeStampDateRepeat);

                // get from time of old task
                Calendar calendarFromTimeOldTask = Calendar.getInstance();
                calendarFromTimeOldTask.setTime(taskNeedDuplicate.getFromTime());

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date(timeStampDateRepeat));
                // change from time
                calendar.set(Calendar.HOUR, calendarFromTimeOldTask.get(Calendar.HOUR));
                calendar.set(Calendar.MINUTE, calendarFromTimeOldTask.get(Calendar.MINUTE));
                calendar.set(Calendar.SECOND, calendarFromTimeOldTask.get(Calendar.SECOND));
                calendar.set(Calendar.MILLISECOND, calendarFromTimeOldTask.get(Calendar.MILLISECOND));
                taskDuplicate.setFromTime(calendar.getTime());

                // change to time
                Calendar calendarToTimeOldTask = Calendar.getInstance();
                calendarToTimeOldTask.setTime(taskNeedDuplicate.getToTime());
                // change from time
                calendar.set(Calendar.HOUR, calendarToTimeOldTask.get(Calendar.HOUR));
                calendar.set(Calendar.MINUTE, calendarToTimeOldTask.get(Calendar.MINUTE));
                calendar.set(Calendar.SECOND, calendarToTimeOldTask.get(Calendar.SECOND));
                calendar.set(Calendar.MILLISECOND, calendarToTimeOldTask.get(Calendar.MILLISECOND));
                taskDuplicate.setToTime(calendar.getTime());

                taskDuplicate.setStatus(Constants.status.ASSIGNED.toString());

                // information need to unique for not duplicate redundant
                taskDuplicate.setRepeatOn(null);
                taskDuplicate.setIsDuplicateTask(taskNeedDuplicate.getTaskId());

                Task taskResultData = taskRepository.save(taskDuplicate);
                if (taskResultData != null) {
                    result.add(taskResultData);
                }
            }
        }
        return result;
    }

    public List<Task> getTaskDuplicateOnThatDate(Long childId, Long timeStampDateRepeat) {
        List<Task> result = new ArrayList<>();
        List<Task> allTaskHasRepeat = taskRepository.findTasksByChildChildIdAndIsDeletedAndRepeatOnIsNotNull(childId, Boolean.FALSE);

        // find all the task that need duplicate
        int indexOfDayRepeat = Util.fromTimeStampToDayInWeek(timeStampDateRepeat);
        List<Task> taskNeedToRepeat = new ArrayList<>();
        for (Task task : allTaskHasRepeat) {
            // check the string repeat on of task at index of n is 1
            // if is 1 that's the task need to duplicate in the system
            if (String.format("%c", task.getRepeatOn().charAt(indexOfDayRepeat)).equals("1")
                    && task.getCreatedDate() < Util.getStartDay(timeStampDateRepeat)
                    && !Util.isExceptionDate(new ArrayList<>(task.getTask_ExceptionDateCollection()), timeStampDateRepeat)
                    && Util.getStartDay(task.getAssignDate()).longValue() != Util.getStartDay(timeStampDateRepeat)) {
                taskNeedToRepeat.add(task);
            }
        }
        // end find all the task that need duplicate

        // duplicate task and save to the system
        if (!taskNeedToRepeat.isEmpty()) {
            for (Task taskNeedDuplicate : taskNeedToRepeat) {
                Task taskDuplicate = new Task();

                // basic information that's not change
                taskDuplicate.setTaskId(taskNeedDuplicate.getTaskId());
                taskDuplicate.setName(taskNeedDuplicate.getName());
                taskDuplicate.setIsDeleted(taskNeedDuplicate.getIsDeleted());
                taskDuplicate.setType(taskNeedDuplicate.getType());

                // get from time of old task
                Calendar calendarFromTimeOldTask = Calendar.getInstance();
                calendarFromTimeOldTask.setTime(taskNeedDuplicate.getFromTime());

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date(timeStampDateRepeat));
                // change from time
                calendar.set(Calendar.HOUR, calendarFromTimeOldTask.get(Calendar.HOUR));
                calendar.set(Calendar.MINUTE, calendarFromTimeOldTask.get(Calendar.MINUTE));
                calendar.set(Calendar.SECOND, calendarFromTimeOldTask.get(Calendar.SECOND));
                calendar.set(Calendar.MILLISECOND, calendarFromTimeOldTask.get(Calendar.MILLISECOND));
                taskDuplicate.setFromTime(calendar.getTime());

                // change to time
                Calendar calendarToTimeOldTask = Calendar.getInstance();
                calendarToTimeOldTask.setTime(taskNeedDuplicate.getToTime());
                // change from time
                calendar.set(Calendar.HOUR, calendarToTimeOldTask.get(Calendar.HOUR));
                calendar.set(Calendar.MINUTE, calendarToTimeOldTask.get(Calendar.MINUTE));
                calendar.set(Calendar.SECOND, calendarToTimeOldTask.get(Calendar.SECOND));
                calendar.set(Calendar.MILLISECOND, calendarToTimeOldTask.get(Calendar.MILLISECOND));
                taskDuplicate.setToTime(calendar.getTime());

                taskDuplicate.setStatus(Constants.status.ASSIGNED.toString());

                result.add(taskDuplicate);
            }
        }
        return result;
    }

    public List<ListTaskResponseDTO> getChildListOfTask(List<Task> taskOfChild, String provider) {
        List<ListTaskResponseDTO> result = new ArrayList<>();
        if (taskOfChild != null) {
            for (Task taskResult: taskOfChild) {
                ListTaskResponseDTO resultData = new ListTaskResponseDTO();

                resultData.setName(taskResult.getName());
                resultData.setStatus(taskResult.getStatus());
                resultData.setTaskId(taskResult.getTaskId());
                resultData.setFromTime(Util.formatTimestampToTime(taskResult.getFromTime().getTime()));
                resultData.setToTime(Util.formatTimestampToTime(taskResult.getToTime().getTime()));
                resultData.setType(taskResult.getType());
                if (provider != null && provider.equalsIgnoreCase(Constants.SMART_WATCH)) {
                    resultData.setPhoto(Util.fromImageFileToBase64String(taskResult.getProofPhoto()));
                    resultData.setDescription(taskResult.getDescription());
                }

                result.add(resultData);
            }
        }
        return result;
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
}
