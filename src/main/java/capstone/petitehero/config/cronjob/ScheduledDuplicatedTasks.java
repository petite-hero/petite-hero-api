package capstone.petitehero.config.cronjob;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.request.location.PushNotiSWDTO;
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
import java.util.stream.Collectors;

@Component
public class ScheduledDuplicatedTasks {

    @Autowired
    private TaskRepository taskRepository;

    @Scheduled(cron = Constants.CRON_SCHEDULED, zone = Constants.TIME_ZONE)
    public void cronJobTasks() {
        List<Task> taskList = taskRepository.findTasksByIsDeletedAndAssignDateBetween(
                Boolean.FALSE, Util.getStartDay(new Date().getTime()), Util.getEndDay(new Date().getTime()));

        List<Task> distinctChildList = taskList
                .stream()
                .filter(Util.distinctByKey(Task::getChild))
                .collect(Collectors.toList());

        PushNotiSWDTO noti = new PushNotiSWDTO(Constants.SILENT_NOTI, Constants.NEW_SAFEZONES, null);

        for (Task childHasTaskAtCurrentDay : taskList) {
            if (childHasTaskAtCurrentDay.getChild().getPushToken() != null
                    && !childHasTaskAtCurrentDay.getChild().getPushToken().isEmpty()) {
                String pushToken = childHasTaskAtCurrentDay.getChild().getPushToken();

            }
        }
    }
}
