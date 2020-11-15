package capstone.petitehero.services;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.ResponseObject;
import capstone.petitehero.dtos.common.CRONJobChildDTO;
import capstone.petitehero.dtos.request.location.PushNotiSWDTO;
import capstone.petitehero.dtos.response.location.GetListSafeZoneByDateResponseDTO;
import capstone.petitehero.entities.Child;
import capstone.petitehero.entities.Safezone;
import capstone.petitehero.repositories.ChildRepository;
import capstone.petitehero.repositories.SafeZoneRepository;
import capstone.petitehero.utilities.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CRONJobService {

    @Autowired
    private NotificationService notiService;

    @Autowired
    private SafeZoneRepository safeZoneRepository;

    @Autowired
    private ChildRepository childRepository;

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
