package capstone.petitehero.config.cronjob;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.request.location.PushNotiSWDTO;
import capstone.petitehero.dtos.response.task.ListTaskResponseDTO;
import capstone.petitehero.entities.Task;
import capstone.petitehero.repositories.TaskRepository;
import capstone.petitehero.services.NotificationService;
import capstone.petitehero.utilities.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ScheduledDuplicatedTasks {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private NotificationService notiService;

    @Scheduled(cron = Constants.CRON_SCHEDULED, zone = Constants.TIME_ZONE)
    public void cronJobTasks() {
        List<Task> taskList = taskRepository.findTasksByIsDeletedAndAssignDateBetween(
                Boolean.FALSE, Util.getStartDay(new Date().getTime()), Util.getEndDay(new Date().getTime()));

        List<Task> distinctChildList = taskList
                .stream()
                .filter(Util.distinctByKey(Task::getChild))
                .collect(Collectors.toList());

        PushNotiSWDTO noti = new PushNotiSWDTO(Constants.SILENT_NOTI, Constants.NEW_TASKS, null);

        for (Task childHasTaskAtCurrentDay : distinctChildList) {
            if (childHasTaskAtCurrentDay.getChild().getPushToken() != null
                    && !childHasTaskAtCurrentDay.getChild().getPushToken().isEmpty()) {
                String pushToken = childHasTaskAtCurrentDay.getChild().getPushToken();

                List<ListTaskResponseDTO> listTask = Util.notiTasksAtCurrentDateForChild(taskList.stream()
                        .filter(task ->
                                task.getChild().getChildId().longValue()
                                        == childHasTaskAtCurrentDay.getChild().getChildId().longValue())
                        .collect(Collectors.toList()));

                if (!listTask.isEmpty()) {
                    noti.setData(listTask);
                    notiService.pushNotificationSW(noti, pushToken);
                }
            }
        }
    }
}
