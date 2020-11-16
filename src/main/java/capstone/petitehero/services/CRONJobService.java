package capstone.petitehero.services;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.ResponseObject;
import capstone.petitehero.dtos.common.CRONJobChildDTO;
import capstone.petitehero.dtos.common.LicenseDTO;
import capstone.petitehero.dtos.request.location.PushNotiSWDTO;
import capstone.petitehero.dtos.response.location.GetListSafeZoneByDateResponseDTO;
import capstone.petitehero.dtos.response.task.ListTaskResponseDTO;
import capstone.petitehero.entities.Child;
import capstone.petitehero.entities.Parent;
import capstone.petitehero.entities.Safezone;
import capstone.petitehero.entities.Task;
import capstone.petitehero.repositories.ChildRepository;
import capstone.petitehero.repositories.ParentRepository;
import capstone.petitehero.repositories.SafeZoneRepository;
import capstone.petitehero.repositories.TaskRepository;
import capstone.petitehero.utilities.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CRONJobService {

    @Autowired
    private NotificationService notiService;

    @Autowired
    private SafeZoneRepository safeZoneRepository;

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private ConfigService configService;

    @Autowired
    private TaskRepository taskRepository;

    @Scheduled(cron = "0 05 00 * * ?")
    public void cronJobSafeZone () {
        try {
            Long currentDateMilli = Util.getCurrentDateMilliValue();
            String currentWeekdayRegex = Util.getCurrentWeekdayRegex();
            PushNotiSWDTO noti = new PushNotiSWDTO(Constants.SILENT_NOTI, Constants.NEW_SAFEZONES, null);

            List<Object[]> rawList = childRepository.getChildListBySafeZones(Util.getCurrentDateMilliValue());
            List<CRONJobChildDTO> childList =  Util.castToCronObject(rawList);
            for (CRONJobChildDTO currentChild : childList) {
                String pushToken = currentChild.getPushToken();
                List<Safezone> rawSafeZoneList = safeZoneRepository.getListSafeZone(currentChild.getChildId(), currentDateMilli, currentWeekdayRegex);
                List<GetListSafeZoneByDateResponseDTO> safezoneList = Util.castToSafeZoneResponse(rawSafeZoneList);

                if (safezoneList.size() != 0 && pushToken != null) {
                    noti.setData(safezoneList);
                    notiService.pushNotificationSW(noti, pushToken);
                }
            }
        } catch (Exception e) {
            System.out.println("===> Error at CRON job SafeZone: " + e.toString());
            e.printStackTrace();
        }
    }

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

    public ResponseObject testCRONJobSafeZone (Long childId) {
        ResponseObject result = Util.createResponse();
        try {
            Child child = childRepository.getOne(childId);
            if (child == null) {
                result.setData(null);
                result.setMsg("Bad request - Child doesn't exist");
                result.setCode(Constants.CODE_400);
            } else {
                Long currentDateMilli = Util.getCurrentDateMilliValue();
                String currentWeekdayRegex = Util.getCurrentWeekdayRegex();
                PushNotiSWDTO noti = new PushNotiSWDTO(Constants.SILENT_NOTI, Constants.NEW_SAFEZONES, null);

                List<Safezone> rawData = safeZoneRepository.getListSafeZone(childId, currentDateMilli, currentWeekdayRegex);
                List<GetListSafeZoneByDateResponseDTO> filteredData = Util.castToSafeZoneResponse(rawData);

                if (filteredData.size() > 0) {
                    noti.setData(filteredData);

                    String pushToken = child.getPushToken();
                    if (pushToken != null && !pushToken.isEmpty()) {
                        notiService.pushNotificationSW(noti, child.getPushToken());
                    } else {
                        result.setMsg("Child pushToken null đcmm");
                    }
                } else {
                    result.setMsg("No suitable data with your request");
                }
            }
        } catch (Exception e) {
            result.setData(null);
            result.setMsg(Constants.SERVER_ERROR + e.toString());
            result.setCode(Constants.CODE_500);
            System.out.println("===> Error at testCRONJobSafeZone: " + e.toString());
            e.printStackTrace();
        }
        return result;
    }
}
