package capstone.petitehero.config.cronjob;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.common.LicenseDTO;
import capstone.petitehero.dtos.request.location.PushNotiSWDTO;
import capstone.petitehero.dtos.response.task.ListTaskResponseDTO;
import capstone.petitehero.entities.Child;
import capstone.petitehero.entities.Parent;
import capstone.petitehero.entities.Task;
import capstone.petitehero.repositories.ParentRepository;
import capstone.petitehero.repositories.TaskRepository;
import capstone.petitehero.services.ConfigService;
import capstone.petitehero.services.NotificationService;
import capstone.petitehero.utilities.Util;
import capstone.petitehero.utilities.XMLUtil;
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

    @Autowired
    private NotificationService notiService;

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private ConfigService configService;

    @Scheduled(cron = Constants.CRON_SCHEDULED, zone = Constants.TIME_ZONE)
    public void cronJobTasks() {
        List<Task> taskList = taskRepository.findTasksByIsDeletedAndAssignDateIsBetween(
                Boolean.FALSE, Util.getStartDay(new Date().getTime()), Util.getEndDay(new Date().getTime()));

        if (taskList != null && !taskList.isEmpty()) {
            List<Task> distinctChildList = taskList
                    .stream()
                    .filter(Util.distinctByKey(Task::getChild))
                    .collect(Collectors.toList());

            PushNotiSWDTO noti = new PushNotiSWDTO(Constants.SILENT_NOTI, Constants.NEW_TASKS, null);

            //cron job for all children
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

    // noti for parent account who account is about to expired
    public void cronjobParentSubscriptions() {
        LicenseDTO licenseDTO = configService.getLicenseForAdmin();
        Long currentDay = Util.getStartDay(new Date().getTime());

        // get expired date subscription noty (current is 14 days)
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentDay);
        calendar.add(Calendar.DATE, licenseDTO.getExpired_date_subscription_noti().shortValue());

        List<Parent> parentList =
                parentRepository.getParentsByIsDisabledAndSubscription_ExpiredDateIsBetween(
                        Boolean.FALSE, currentDay, Util.getEndDay(calendar.getTimeInMillis()));

        if (parentList != null && !parentList.isEmpty()) {
            for (Parent parent : parentList) {
                ArrayList<String> pushTokenList = new ArrayList<>();
                if (parent.getPushToken() != null && !parent.getPushToken().isEmpty()) {
                    pushTokenList.add(parent.getPushToken());
                    notiService.pushNotificationMobile(Constants.PETITE_HERO,
                            "Your account in Petite Hero is about to expired in " + Util.formatTimestampToDate(calendar.getTimeInMillis()),
                            pushTokenList);
                }
            }
        }
    }
}
