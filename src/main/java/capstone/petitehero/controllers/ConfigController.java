package capstone.petitehero.controllers;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.ResponseObject;
import capstone.petitehero.dtos.common.LicenseDTO;
import capstone.petitehero.services.ConfigService;
import capstone.petitehero.utilities.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ConfigController {

    @Autowired
    private ConfigService configService;

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
        if (modifyLicenseDTO.getTotal_hour_task() != null && !modifyLicenseDTO.getTotal_hour_task().toString().isEmpty()) {
            if (!Util.validateLongNumber(modifyLicenseDTO.getTotal_hour_task().toString())) {
                responseObject = new ResponseObject(Constants.CODE_400, "Report delay should only contain number");
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
            }
            licenseDTO.setTotal_hour_task(modifyLicenseDTO.getTotal_hour_task());
        }
        if (modifyLicenseDTO.getSafezone_cron_time() != null && !modifyLicenseDTO.getSafezone_cron_time().isEmpty()) {
            if (modifyLicenseDTO.getSafezone_cron_time().matches("^(?:(?:([01]?\\d|2[0-3]):)?([0-5]?\\d):)?([0-5]?\\d)$")) {
                responseObject = new ResponseObject(Constants.CODE_400, "Safezone cron time should in format HH:MM:ss");
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
            }
            licenseDTO.setSafezone_cron_time(modifyLicenseDTO.getSafezone_cron_time());
        }
        if (modifyLicenseDTO.getTask_cron_time() != null && !modifyLicenseDTO.getTask_cron_time().isEmpty()) {
            if (modifyLicenseDTO.getTask_cron_time().matches("^(?:(?:([01]?\\d|2[0-3]):)?([0-5]?\\d):)?([0-5]?\\d)$")) {
                responseObject = new ResponseObject(Constants.CODE_400, "Task cron time should in format HH:MM:ss");
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
            }
            licenseDTO.setTask_cron_time(modifyLicenseDTO.getTask_cron_time());
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
}
