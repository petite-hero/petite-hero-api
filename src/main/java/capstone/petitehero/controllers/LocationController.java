package capstone.petitehero.controllers;

import capstone.petitehero.dtos.ResponseObject;
import capstone.petitehero.dtos.request.location.AddLocationRequestDTO;
import capstone.petitehero.dtos.request.location.AddNewSafeZoneRequestDTO;
import capstone.petitehero.dtos.request.location.GetListByTimeRequestDTO;
import capstone.petitehero.dtos.request.location.UpdateSafeZoneRequestDTO;
import capstone.petitehero.services.LocationService;
import capstone.petitehero.services.SafeZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/location")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @Autowired
    private SafeZoneService safeZoneService;


    @RequestMapping(value = "/current-location/{emergency}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseObject addNewLocationFromSW(@RequestBody AddLocationRequestDTO location, @PathVariable Boolean emergency) {
        return locationService.recordLocationFromSW(location, emergency);
    }

    @RequestMapping(value = "/list/{child}/{from}/{to}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseObject getListByTime(@PathVariable(value = "child") Long child,
                                        @PathVariable(value = "from") Long from,
                                        @PathVariable(value = "to") Long to) {
        return locationService.getListByTime(child, from, to);
    }

    @RequestMapping(value = "/list/{child}/{date}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseObject getListByDate(@PathVariable(value = "child") Long child,
                                        @PathVariable(value = "date") Long date) {
        return safeZoneService.getListByDate(child, date);
    }

    @RequestMapping(value = "/latest/{child}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseObject getLatestChildLocation(@PathVariable(value = "child") Long child) {
        return locationService.getLatestChildLocation(child);
    }

    @RequestMapping(value = "/safezone", method = RequestMethod.POST)
    @ResponseBody
    public ResponseObject addNewSafeZone(@RequestBody AddNewSafeZoneRequestDTO safezone) {
        return safeZoneService.addSafeZone(safezone);
    }

    @RequestMapping(value = "/safezone/{safezoneId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseObject getSafeZoneDetail(@PathVariable(value = "safezoneId") Long safezoneId) {
        return safeZoneService.getSafeZoneDetail(safezoneId);
    }

    @RequestMapping(value = "/emergency/{child}/{emergency}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseObject updateEmergencyState(@PathVariable(value = "child") Long child, @PathVariable(value = "emergency") Boolean emergency) {
        return locationService.updateEmergencyStatus(child ,emergency);
    }

    @RequestMapping(value = "/safezone/{safezoneId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseObject deleteSafeZone(@PathVariable(value = "safezoneId") Long safezoneId) {
        return safeZoneService.deleteSafeZone(safezoneId);
    }

    @RequestMapping(value = "/safezone", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseObject updateSafeZone(@RequestBody UpdateSafeZoneRequestDTO dto) {
        return safeZoneService.updateSafeZone(dto);
    }
}