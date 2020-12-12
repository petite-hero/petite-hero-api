package capstone.petitehero.controllers;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.ResponseObject;
import capstone.petitehero.dtos.common.LicenseDTO;
import capstone.petitehero.repositories.LocationRepository;
import capstone.petitehero.services.ConfigService;
import capstone.petitehero.utilities.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.TemporalUnit;
import java.util.Calendar;
import java.util.Date;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ConfigController {

    @Autowired
    private ConfigService configService;

    @Autowired
    private LocationRepository locationRepository;

    @Value("${spring.datasource.database}")
    private String database;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.host}")
    private String host;

    @Value("${spring.datasource.port}")
    private String port;

    @RequestMapping(value = "/config", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getLicenseForAdmin() {
        ResponseObject responseObject;

        LicenseDTO result = configService.getLicenseForAdmin();
        if (result != null) {
            responseObject = new ResponseObject(Constants.CODE_200, "OK");
            responseObject.setData(result);
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }

        responseObject = new ResponseObject(Constants.CODE_500, "Cannot get license in the system");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/config", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<Object> modifyLicenseForAdmin(@RequestBody LicenseDTO modifyLicenseDTO) {
        ResponseObject responseObject;

        LicenseDTO licenseDTO = configService.getLicenseForAdmin();
        if (modifyLicenseDTO.getOuter_radius() != null && !modifyLicenseDTO.getOuter_radius().toString().isEmpty()) {
            if (!Util.validateLongNumber(modifyLicenseDTO.getOuter_radius().toString())) {
                responseObject = new ResponseObject(Constants.CODE_400, "Outer radius should only contain number");
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
            }
            licenseDTO.setOuter_radius(modifyLicenseDTO.getOuter_radius());
        }
        if (modifyLicenseDTO.getReport_delay() != null && !modifyLicenseDTO.getReport_delay().toString().isEmpty()) {
            if (!Util.validateLongNumber(modifyLicenseDTO.getReport_delay().toString())) {
                responseObject = new ResponseObject(Constants.CODE_400, "Report delay should only contain number");
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
            }
            licenseDTO.setReport_delay(modifyLicenseDTO.getReport_delay());
        }
        if (modifyLicenseDTO.getTotal_hour_task_education() != null && !modifyLicenseDTO.getTotal_hour_task_education().toString().isEmpty()) {
            if (!Util.validateLongNumber(modifyLicenseDTO.getTotal_hour_task_education().toString())) {
                responseObject = new ResponseObject(Constants.CODE_400, "Total hour task education should only contain number");
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
            }
            licenseDTO.setTotal_hour_task_education(modifyLicenseDTO.getTotal_hour_task_education());
        }
        if (modifyLicenseDTO.getTotal_hour_task_housework() != null && !modifyLicenseDTO.getTotal_hour_task_housework().toString().isEmpty()) {
            if (!Util.validateLongNumber(modifyLicenseDTO.getTotal_hour_task_housework().toString())) {
                responseObject = new ResponseObject(Constants.CODE_400, "Total hour task housework should only contain number");
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
            }
            licenseDTO.setTotal_hour_task_housework(modifyLicenseDTO.getTotal_hour_task_housework());
        }
        if (modifyLicenseDTO.getTotal_hour_task_skills() != null && !modifyLicenseDTO.getTotal_hour_task_skills().toString().isEmpty()) {
            if (!Util.validateLongNumber(modifyLicenseDTO.getTotal_hour_task_skills().toString())) {
                responseObject = new ResponseObject(Constants.CODE_400, "Total hour task skills should only contain number");
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
            }
            licenseDTO.setTotal_hour_task_skills(modifyLicenseDTO.getTotal_hour_task_skills());
        }
        if (modifyLicenseDTO.getSafezone_cron_time() != null && !modifyLicenseDTO.getSafezone_cron_time().isEmpty()) {
            if (!modifyLicenseDTO.getSafezone_cron_time().matches("^(?:(?:([01]?\\d|2[0-3]):)?([0-5]?\\d):)?([0-5]?\\d)$")) {
                responseObject = new ResponseObject(Constants.CODE_400, "Safezone cron time should in format HH:MM:ss");
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
            }
            licenseDTO.setSafezone_cron_time(modifyLicenseDTO.getSafezone_cron_time());
        }
        if (modifyLicenseDTO.getTask_cron_time() != null && !modifyLicenseDTO.getTask_cron_time().isEmpty()) {
            if (!modifyLicenseDTO.getTask_cron_time().matches("^(?:(?:([01]?\\d|2[0-3]):)?([0-5]?\\d):)?([0-5]?\\d)$")) {
                responseObject = new ResponseObject(Constants.CODE_400, "Task cron time should in format HH:MM:ss");
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
            }
            licenseDTO.setTask_cron_time(modifyLicenseDTO.getTask_cron_time());
        }
        if (modifyLicenseDTO.getQuest_cron_time() != null && !modifyLicenseDTO.getQuest_cron_time().isEmpty()) {
            if (!modifyLicenseDTO.getQuest_cron_time().matches("^(?:(?:([01]?\\d|2[0-3]):)?([0-5]?\\d):)?([0-5]?\\d)$")) {
                responseObject = new ResponseObject(Constants.CODE_400, "Quest cron time should in format HH:MM:ss");
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
            }
            licenseDTO.setQuest_cron_time(modifyLicenseDTO.getQuest_cron_time());
        }
        if (modifyLicenseDTO.getParent_subscription_cron_time() != null && !modifyLicenseDTO.getParent_subscription_cron_time().isEmpty()) {
            if (!modifyLicenseDTO.getParent_subscription_cron_time().matches("^(?:(?:([01]?\\d|2[0-3]):)?([0-5]?\\d):)?([0-5]?\\d)$")) {
                responseObject = new ResponseObject(Constants.CODE_400, "Parent subscription cron time should in format HH:MM:ss");
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
            }
            licenseDTO.setParent_subscription_cron_time(modifyLicenseDTO.getParent_subscription_cron_time());
        }
        if (modifyLicenseDTO.getExpired_date_subscription_noti() != null && !modifyLicenseDTO.getExpired_date_subscription_noti().toString().isEmpty()) {
            if (!Util.validateLongNumber(modifyLicenseDTO.getExpired_date_subscription_noti().toString())) {
                responseObject = new ResponseObject(Constants.CODE_400, "Expired date subscription should only contain number");
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
            }
            licenseDTO.setExpired_date_subscription_noti(modifyLicenseDTO.getExpired_date_subscription_noti());
        }
        if (modifyLicenseDTO.getFailed_task_cron_time() != null && !modifyLicenseDTO.getFailed_task_cron_time().toString().isEmpty()) {
            if (!modifyLicenseDTO.getFailed_task_cron_time().matches("^(?:(?:([01]?\\d|2[0-3]):)?([0-5]?\\d):)?([0-5]?\\d)$")) {
                responseObject = new ResponseObject(Constants.CODE_400, "Quest cron time should in format HH:MM:ss");
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
            }
            licenseDTO.setFailed_task_cron_time(modifyLicenseDTO.getFailed_task_cron_time());
        }
        if (modifyLicenseDTO.getLicense_EN() != null && !modifyLicenseDTO.getLicense_EN().isEmpty()) {
            licenseDTO.setLicense_EN(modifyLicenseDTO.getLicense_EN());
        }
        if (modifyLicenseDTO.getLicense_VN() != null && !modifyLicenseDTO.getLicense_VN().isEmpty()) {
            licenseDTO.setLicense_VN(modifyLicenseDTO.getLicense_VN());
        }

        String result = configService.modifyLicense(licenseDTO);
        if (result != null) {
            responseObject = new ResponseObject(Constants.CODE_200, "OK");
            responseObject.setData(result);
            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }

        responseObject = new ResponseObject(Constants.CODE_500, "Cannot modify license in the system");
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "welcome", method = RequestMethod.GET)
    public String welcomeAPI() {
        return "You have started Petite Hero server successfully.";
    }

    @RequestMapping(value = "/test/dump-database", method = RequestMethod.POST)
    public ResponseEntity<Object> dumpDatabase() {
        ResponseObject responseObject;
        File file = new File("log-" + new Date().getTime() + ".txt");
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));

            String outputFile = "backup-database-" + new Date().getTime() + ".sql";
            int processComplete = 0;
            Process process;
            try {
                // -u for database username
                // -p for database password
                // -P for database port
                // -h for database host (ip address)
                // --databases for choosing database to dump
                // -r output file
                String command = String.format("mysqldump -u %s -p%s -P %s -h %s --add-drop-table --databases %s -r %s",
                        username, password, port, host, database, outputFile);
                process = Runtime.getRuntime().exec(command);
            } catch (IOException ioException) {
                bufferedWriter.write(ioException.getMessage());
                bufferedWriter.close();

                responseObject = new ResponseObject(Constants.CODE_500,
                        "Has problem when backup database. Reason: " + ioException.getMessage());
                return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            try {
                if (process != null) {
                    processComplete = process.waitFor();
                }
            } catch (InterruptedException interruptedException) {
                bufferedWriter.write(interruptedException.getMessage());
                bufferedWriter.close();

                responseObject = new ResponseObject(Constants.CODE_500,
                        "Has problem when backup database. Reason: " + interruptedException.getMessage());
                return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            if (processComplete == 0) {
                locationRepository.deleteAll();
                locationRepository.resetGeneratedIdInLocationHistoryTable();
                bufferedWriter.write("Backup database successfully.");
                bufferedWriter.close();

                responseObject = new ResponseObject(Constants.CODE_200, "Backup database successfully.");
                return new ResponseEntity<>(responseObject, HttpStatus.OK);
            } else {
                bufferedWriter.write("Cannot backup database");
                bufferedWriter.close();

                responseObject = new ResponseObject(Constants.CODE_500, "Cannot backup database.");
                return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (IOException ioException) {
            responseObject = new ResponseObject(Constants.CODE_500, "Cannot write log file.");
            return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
