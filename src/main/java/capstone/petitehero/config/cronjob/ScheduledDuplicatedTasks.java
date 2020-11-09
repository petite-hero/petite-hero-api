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

    @Scheduled(cron = Constants.CRON_SCHEDULED, zone = Constants.TIME_ZONE)
    public void duplicatedTasks() {
    }
}
