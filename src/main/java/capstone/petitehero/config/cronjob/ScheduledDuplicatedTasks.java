package capstone.petitehero.config.cronjob;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.entities.Task;
import capstone.petitehero.repositories.TaskRepository;
import capstone.petitehero.services.TaskService;
import capstone.petitehero.utilities.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class ScheduledDuplicatedTasks {

    @Autowired
    private TaskRepository taskRepository;

    @Scheduled(cron = Constants.CRON_SCHEDULED, zone = Constants.TIME_ZONE)
    public void duplicatedTasks() {
        Date today = new Date();
        int indexOfDayRepeat = Util.fromTimeStampToDayInWeek(today.getTime());

        List<Task> allTaskHasRepeat =
                taskRepository.findTasksByIsDeletedAndRepeatOnIsNotNull(Boolean.FALSE);

        List<Task> taskNeedToRepeat = new ArrayList<>();
        for (Task task : allTaskHasRepeat) {
            // check the string repeat on of task at index of n is 1
            // if is 1 that's the task need to duplicate in the system
            if (String.format("%c", task.getRepeatOn().charAt(indexOfDayRepeat)).equals("1")) {
                taskNeedToRepeat.add(task);
            }
        }
        if (!taskNeedToRepeat.isEmpty()) {
            for (Task task : taskNeedToRepeat) {
                Task taskDuplicate = new Task();

                // basic information that's not change
                taskDuplicate.setName(task.getName());
                taskDuplicate.setDescription(task.getDescription());
                taskDuplicate.setParent(task.getParent());
                taskDuplicate.setChild(task.getChild());
                taskDuplicate.setCreatedDate(task.getCreatedDate());
                taskDuplicate.setIsDeleted(Boolean.FALSE);
                taskDuplicate.setType(task.getType());

                // information that's change
                taskDuplicate.setAssignDate(today.getTime());

                // get from time of old task
                Calendar calendarFromTimeOldTask = Calendar.getInstance();
                calendarFromTimeOldTask.setTime(task.getFromTime());

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(today);
                // change from time
                calendar.set(Calendar.HOUR, calendarFromTimeOldTask.get(Calendar.HOUR));
                calendar.set(Calendar.MINUTE, calendarFromTimeOldTask.get(Calendar.MINUTE));
                calendar.set(Calendar.SECOND, calendarFromTimeOldTask.get(Calendar.SECOND));
                calendar.set(Calendar.MILLISECOND, calendarFromTimeOldTask.get(Calendar.MILLISECOND));
                taskDuplicate.setFromTime(calendar.getTime());

                // change to time
                Calendar calendarToTimeOldTask = Calendar.getInstance();
                calendarToTimeOldTask.setTime(task.getToTime());
                // change from time
                calendar.set(Calendar.HOUR, calendarToTimeOldTask.get(Calendar.HOUR));
                calendar.set(Calendar.MINUTE, calendarToTimeOldTask.get(Calendar.MINUTE));
                calendar.set(Calendar.SECOND, calendarToTimeOldTask.get(Calendar.SECOND));
                calendar.set(Calendar.MILLISECOND, calendarToTimeOldTask.get(Calendar.MILLISECOND));
                taskDuplicate.setToTime(calendar.getTime());


                taskDuplicate.setStatus("ASSIGNED");

                // information need to unique for not duplicate redundant
                taskDuplicate.setRepeatOn(null);
                taskDuplicate.setIsDuplicateTask(task.getTaskId());

                try {
                    taskRepository.save(taskDuplicate);
                } catch (Exception e) {
                    System.out.println("Error: " + e.toString());
                }
            }
        }
    }
}
