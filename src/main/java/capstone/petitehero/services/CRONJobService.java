package capstone.petitehero.services;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.ResponseObject;
import capstone.petitehero.dtos.common.CRONJobChildDTO;
import capstone.petitehero.dtos.common.LicenseDTO;
import capstone.petitehero.dtos.common.NotificationDTO;
import capstone.petitehero.dtos.request.location.PushNotiSWDTO;
import capstone.petitehero.dtos.response.location.GetListSafeZoneByDateResponseDTO;
import capstone.petitehero.dtos.response.quest.ListQuestResponseDTO;
import capstone.petitehero.dtos.response.task.ListTaskResponseDTO;
import capstone.petitehero.entities.*;
import capstone.petitehero.repositories.*;
import capstone.petitehero.utilities.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

@Configuration
public class CRONJobService implements SchedulingConfigurer {

    @Autowired
    private NotificationService notiService;

    @Autowired
    private SafeZoneRepository safeZoneRepository;

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private ConfigService configService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private QuestRepository questRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ParentChildRepository parentChildRepository;

    @Value("${spring.datasource.database}")
    private String database;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public ResponseObject testCRONJobSafeZone(Long childId) {
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

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {

        LicenseDTO licenseDTO = configService.getLicenseForAdmin();
        String questCronExpression = Util.formatTimeCronjob(licenseDTO.getQuest_cron_time());
        String taskCronExpression = Util.formatTimeCronjob(licenseDTO.getTask_cron_time());
        String safezoneCronExpression = Util.formatTimeCronjob(licenseDTO.getSafezone_cron_time());
        String parentSubscriptionCronExpression = Util.formatTimeCronjob(licenseDTO.getParent_subscription_cron_time());
        String failedTaskCronExpression = Util.formatTimeCronjob(licenseDTO.getFailed_task_cron_time());

        // dynamic cronjob for noti safezone
        scheduledTaskRegistrar.addTriggerTask(new Runnable() {
            @Override
            public void run() {
                try {
                    Long currentDateMilli = Util.getCurrentDateMilliValue();
                    String currentWeekdayRegex = Util.getCurrentWeekdayRegex();
                    PushNotiSWDTO noti = new PushNotiSWDTO(Constants.SILENT_NOTI, Constants.NEW_SAFEZONES, null);

                    List<Object[]> rawList = childRepository.getChildListBySafeZones(Util.getCurrentDateMilliValue());
                    List<CRONJobChildDTO> childList = Util.castToCronObject(rawList);
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
        }, new Trigger() {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                LicenseDTO updateLicenseDTO = configService.getLicenseForAdmin();
                String newCronSafezoneExpression = Util.formatTimeCronjob(updateLicenseDTO.getSafezone_cron_time());

                if (!newCronSafezoneExpression.equalsIgnoreCase(safezoneCronExpression)) {
                    scheduledTaskRegistrar.setTriggerTasksList(new ArrayList<>());
                    configureTasks(scheduledTaskRegistrar); // calling recursively.
                    scheduledTaskRegistrar.destroy(); // destroys previously scheduled tasks.
                    scheduledTaskRegistrar.setScheduler(executor);
                    scheduledTaskRegistrar.afterPropertiesSet(); // this will schedule the task with new cron changes.
                    return null; // return null when the cron changed so the trigger will stop.
                }
                CronTrigger crontrigger = new CronTrigger(safezoneCronExpression);
                return crontrigger.nextExecutionTime(triggerContext);
//                LicenseDTO licenseDTO = configService.getLicenseForAdmin();
//                Calendar nextExecutionTime = new GregorianCalendar();
//                Date lastActualExecutionTime = triggerContext.lastActualExecutionTime();
//                nextExecutionTime.setTime(lastActualExecutionTime != null ? lastActualExecutionTime : new Date(Util.getStartDay(new Date().getTime())));
//                Calendar updateDate = Util.formatCronTimeToCalendar(licenseDTO.getSafezone_cron_time());
//                nextExecutionTime.add(Calendar.HOUR_OF_DAY, Util.differentInHour(nextExecutionTime, updateDate));
//                nextExecutionTime.add(Calendar.MINUTE, Util.differentInMinute(nextExecutionTime, updateDate));
//                nextExecutionTime.add(Calendar.SECOND, Util.differentInSecond(nextExecutionTime, updateDate));
//                return nextExecutionTime.getTime();
            }
        });

        // dynamic cronjob for noti task
        scheduledTaskRegistrar.addTriggerTask(new Runnable() {
            @Override
            public void run() {
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
        }, new Trigger() {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                LicenseDTO updateLicenseDTO = configService.getLicenseForAdmin();
                String newCronTaskExpression = Util.formatTimeCronjob(updateLicenseDTO.getTask_cron_time());

                if (!newCronTaskExpression.equalsIgnoreCase(taskCronExpression)) {
                    scheduledTaskRegistrar.setTriggerTasksList(new ArrayList<>());
                    configureTasks(scheduledTaskRegistrar); // calling recursively.
                    scheduledTaskRegistrar.destroy(); // destroys previously scheduled tasks.
                    scheduledTaskRegistrar.setScheduler(executor);
                    scheduledTaskRegistrar.afterPropertiesSet(); // this will schedule the task with new cron changes.
                    return null; // return null when the cron changed so the trigger will stop.
                }
                CronTrigger crontrigger = new CronTrigger(taskCronExpression);
                return crontrigger.nextExecutionTime(triggerContext);
//                LicenseDTO licenseDTO = configService.getLicenseForAdmin();
//                Calendar nextExecutionTime = new GregorianCalendar();
//                Date lastActualExecutionTime = triggerContext.lastActualExecutionTime();
//                nextExecutionTime.setTime(lastActualExecutionTime != null ? lastActualExecutionTime : new Date(Util.getStartDay(new Date().getTime())));
//                Calendar updateDate = Util.formatCronTimeToCalendar(licenseDTO.getTask_cron_time());
//                nextExecutionTime.add(Calendar.HOUR_OF_DAY, Util.differentInHour(nextExecutionTime, updateDate));
//                nextExecutionTime.add(Calendar.MINUTE, Util.differentInMinute(nextExecutionTime, updateDate));
//                nextExecutionTime.add(Calendar.SECOND, Util.differentInSecond(nextExecutionTime, updateDate));
//                return nextExecutionTime.getTime();
            }
        });

        // dynamic cronjob for noti quest
        scheduledTaskRegistrar.addTriggerTask(new Runnable() {
            @Override
            public void run() {
                List<Quest> questList = questRepository.findQuestsByIsDeletedAndStatus(
                        Boolean.FALSE, Constants.status.ASSIGNED.toString());

                if (questList != null && !questList.isEmpty()) {
                    List<Quest> distinctChildList = questList
                            .stream()
                            .filter(Util.distinctByKey(Quest::getChild))
                            .collect(Collectors.toList());

                    PushNotiSWDTO noti = new PushNotiSWDTO(Constants.PETITE_HERO, Constants.NEW_QUESTS, null);

                    //cron job for all children
                    for (Quest childHasQuest : distinctChildList) {
                        if (childHasQuest.getChild().getPushToken() != null
                                && !childHasQuest.getChild().getPushToken().isEmpty()) {
                            String pushToken = childHasQuest.getChild().getPushToken();

                            List<ListQuestResponseDTO> listQuest = Util.getChildListOfQuest(questList.stream()
                                    .filter(task ->
                                            task.getChild().getChildId().longValue()
                                                    == childHasQuest.getChild().getChildId().longValue())
                                    .collect(Collectors.toList()));

                            if (!listQuest.isEmpty()) {
                                noti.setData(listQuest);
                                notiService.pushNotificationSW(noti, pushToken);
                            }
                        }
                    }
                }
            }
        }, new Trigger() {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                LicenseDTO updateLicenseDTO = configService.getLicenseForAdmin();
                String newCronQuestExpression = Util.formatTimeCronjob(updateLicenseDTO.getQuest_cron_time());

                if (!newCronQuestExpression.equalsIgnoreCase(questCronExpression)) {
                    scheduledTaskRegistrar.setTriggerTasksList(new ArrayList<>());
                    configureTasks(scheduledTaskRegistrar); // calling recursively.
                    scheduledTaskRegistrar.destroy(); // destroys previously scheduled tasks.
                    scheduledTaskRegistrar.setScheduler(executor);
                    scheduledTaskRegistrar.afterPropertiesSet(); // this will schedule the task with new cron changes.
                    return null; // return null when the cron changed so the trigger will stop.
                }
                CronTrigger crontrigger = new CronTrigger(questCronExpression);
                return crontrigger.nextExecutionTime(triggerContext);
//                Calendar nextExecutionTime = new GregorianCalendar();
//                Date lastActualExecutionTime = triggerContext.lastActualExecutionTime();
//                nextExecutionTime.setTime(lastActualExecutionTime != null ? lastActualExecutionTime : new Date(Util.getStartDay(new Date().getTime())));
//                Calendar updateDate = Util.formatCronTimeToCalendar(licenseDTO.getQuest_cron_time());
//                nextExecutionTime.add(Calendar.HOUR_OF_DAY, Util.differentInHour(nextExecutionTime, updateDate));
//                nextExecutionTime.add(Calendar.MINUTE, Util.differentInMinute(nextExecutionTime, updateDate));
//                nextExecutionTime.add(Calendar.SECOND, Util.differentInSecond(nextExecutionTime, updateDate));
//                return nextExecutionTime.getTime();
            }
        });

        // dynamic cronjob for noti parent subscription
        scheduledTaskRegistrar.addTriggerTask(new Runnable() {
            @Override
            public void run() {
                LicenseDTO licenseDTO = configService.getLicenseForAdmin();
                Long currentDay = Util.getStartDay(new Date().getTime());

                // get expired date subscription noty (current is 14 days)
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(currentDay);
                calendar.add(Calendar.DATE, licenseDTO.getExpired_date_subscription_noti().shortValue());

                List<Subscription> subscriptionList = subscriptionRepository.findSubscriptionsByIsDisabledAndExpiredDateBetween(
                        Boolean.FALSE, currentDay, Util.getEndDay(calendar.getTimeInMillis()));

                if (subscriptionList != null) {
                    if (!subscriptionList.isEmpty()) {
                        for (Subscription subscription : subscriptionList) {
                            ArrayList<String> pushTokenList = new ArrayList<>();
                            if (subscription.getParent().getPushToken() != null && !subscription.getParent().getPushToken().isEmpty()) {
                                pushTokenList.add(subscription.getParent().getPushToken());
                                notiService.pushNotificationMobile("Your account in Petite Hero is about to expired in " + Util.formatTimestampToDate(calendar.getTimeInMillis()),
                                        new Object(),
                                        pushTokenList);
                            }
                        }
                    }
                }
            }
        }, new Trigger() {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                LicenseDTO updateLicenseDTO = configService.getLicenseForAdmin();
                String newCronParentSubscriptionExpression = Util.formatTimeCronjob(updateLicenseDTO.getParent_subscription_cron_time());

                if (!newCronParentSubscriptionExpression.equalsIgnoreCase(parentSubscriptionCronExpression)) {
                    scheduledTaskRegistrar.setTriggerTasksList(new ArrayList<>());
                    configureTasks(scheduledTaskRegistrar); // calling recursively.
                    scheduledTaskRegistrar.destroy(); // destroys previously scheduled tasks.
                    scheduledTaskRegistrar.setScheduler(executor);
                    scheduledTaskRegistrar.afterPropertiesSet(); // this will schedule the task with new cron changes.
                    return null; // return null when the cron changed so the trigger will stop.
                }
                CronTrigger crontrigger = new CronTrigger(parentSubscriptionCronExpression);
                return crontrigger.nextExecutionTime(triggerContext);

//                LicenseDTO licenseDTO = configService.getLicenseForAdmin();
//                Calendar nextExecutionTime = new GregorianCalendar();
//                Date lastActualExecutionTime = triggerContext.lastActualExecutionTime();
//                nextExecutionTime.setTime(lastActualExecutionTime != null ? lastActualExecutionTime : new Date(Util.getStartDay(new Date().getTime())));
//                Calendar updateDate = Util.formatCronTimeToCalendar(licenseDTO.getParent_subscription_cron_time());
//                nextExecutionTime.add(Calendar.HOUR_OF_DAY, Util.differentInHour(nextExecutionTime, updateDate));
//                nextExecutionTime.add(Calendar.MINUTE, Util.differentInMinute(nextExecutionTime, updateDate));
//                nextExecutionTime.add(Calendar.SECOND, Util.differentInSecond(nextExecutionTime, updateDate));
//                return nextExecutionTime.getTime();
            }
        });

        // cronjob for updating assigned task to failed task
        scheduledTaskRegistrar.addTriggerTask(new Runnable() {
            @Override
            public void run() {
                List<Task> taskList = taskRepository.findTasksByIsDeletedAndAssignDateIsBetweenAndStatus(
                        Boolean.FALSE,
                        Util.getStartDay(new Date().getTime()),
                        Util.getEndDay(new Date().getTime()),
                        Constants.status.ASSIGNED.toString());

                if (taskList != null && !taskList.isEmpty()) {
                    for (Task taskNeedToFailed : taskList) {
//                        taskNeedToFailed.setStatus(Constants.status.FAILED.toString());
                    }

//                    taskRepository.saveAll(taskList);

                    List<Task> distinctChildList = taskList
                            .stream()
                            .filter(Util.distinctByKey(Task::getChild))
                            .collect(Collectors.toList());

                    ArrayList<Parent> parentArrayList = new ArrayList<>();
                    for (Task task : distinctChildList) {
                        List<Parent_Child> parentChildList = parentChildRepository.findParent_ChildrenByChild_ChildIdAndChild_IsDisabled(
                                task.getChild().getChildId(), Boolean.FALSE);

                        if (parentChildList != null) {
                            if (!parentChildList.isEmpty()) {
                                parentArrayList =
                                        Util.checkDuplicateParentNotiList(
                                                parentArrayList, new ArrayList<>(parentChildList));
                            }
                        }
                    }
                    if (!parentArrayList.isEmpty()) {
                        for (Parent parentGetNoti : parentArrayList) {
                            System.out.println("Parent account: " + parentGetNoti.getAccount().getUsername());
                            if (parentGetNoti.getPushToken() != null && !parentGetNoti.getPushToken().isEmpty()) {
                                NotificationDTO notificationDTO = new NotificationDTO();
                                ArrayList<String> pushTokenList = new ArrayList<>();
                                pushTokenList.add(parentGetNoti.getPushToken());
                                notiService.pushNotificationMobile(null, notificationDTO, pushTokenList);
                            }
                        }
                    }
                }
            }
        }, new Trigger() {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                LicenseDTO updateLicenseDTO = configService.getLicenseForAdmin();
                String newCronFailedTaskExpression = Util.formatTimeCronjob(updateLicenseDTO.getFailed_task_cron_time());

                if (!newCronFailedTaskExpression.equalsIgnoreCase(failedTaskCronExpression)) {
                    scheduledTaskRegistrar.setTriggerTasksList(new ArrayList<>());
                    configureTasks(scheduledTaskRegistrar); // calling recursively.
                    scheduledTaskRegistrar.destroy(); // destroys previously scheduled tasks.
                    scheduledTaskRegistrar.setScheduler(executor);
                    scheduledTaskRegistrar.afterPropertiesSet(); // this will schedule the task with new cron changes.
                    return null; // return null when the cron changed so the trigger will stop.
                }
                CronTrigger crontrigger = new CronTrigger(failedTaskCronExpression);
                return crontrigger.nextExecutionTime(triggerContext);
            }
        });

        // cronjob for backup database
        scheduledTaskRegistrar.addTriggerTask(new Runnable() {
            @Override
            public void run() {
                File file = new File("log " + Util.formatTimestampToDate(new Date().getTime()) + ".txt");
                try {
                    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));

                    String outputFile = "backup-database-" + new Date().getTime() + ".sql";
                    int processComplete = 0;
                    Process process = null;
                    try {
                        String command = String.format("mysqldump -u %s -p%s --add-drop-table --databases %s -r %s",
                                username, password, database, outputFile);
                        process = Runtime.getRuntime().exec(command);
                    } catch (IOException ioException) {
                        bufferedWriter.write("Has problem when backup database. Reason: " + ioException.getMessage());
                        bufferedWriter.close();
                    }
                    try {
                        if (process != null) {
                            processComplete = process.waitFor();
                        }
                    } catch (InterruptedException interruptedException) {
                        bufferedWriter.write("Has problem when backup database. Reason: " + interruptedException.getMessage());
                        bufferedWriter.close();
                    }
                    if (processComplete == 0) {
                        locationRepository.deleteAll();
                        locationRepository.resetGeneratedIdInLocationHistoryTable();
                        bufferedWriter.write("Backup database successfully.");
                        bufferedWriter.close();
                    } else {
                        bufferedWriter.write("Cannot backup database");
                        bufferedWriter.close();
                    }
                } catch (IOException ioException) {

                }
            }
        }, new Trigger() {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                CronTrigger crontrigger = new CronTrigger("00 30 00 15 * ?");
                return crontrigger.nextExecutionTime(triggerContext);
            }
        });
    }
}
