package capstone.petitehero.controllers;

import capstone.petitehero.dtos.ResponseObject;
import capstone.petitehero.dtos.request.location.AddLocationRequestDTO;
import capstone.petitehero.dtos.request.location.GetListByTimeRequestDTO;
import capstone.petitehero.services.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/location")
public class LocationController {

    @Autowired
    private LocationService locationService;


    @RequestMapping(value = "/addNewLocation", method = RequestMethod.POST)
    @ResponseBody
    public ResponseObject addNewLocationFromSW(@RequestBody AddLocationRequestDTO location) {
        return locationService.recordLocationFromSW(location);
    }

    @RequestMapping(value = "/list/{child}/{time}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseObject getListByTime(@PathVariable(value = "child") Long child, @PathVariable(value = "time") int time) {
        return locationService.getListByTime(child, time);
//        locationService.pushNotifications("alo", "lolo");
//        return null;
    }

    @RequestMapping(value = "/latest/{child}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseObject getLatestChildLocation(@PathVariable(value = "child") Long child) {
        return locationService.getLatestChildLocation(child);
    }
}
