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

    @RequestMapping(value = "/getListByTime", method = RequestMethod.GET)
    @ResponseBody
    public ResponseObject getListByTime(@RequestBody GetListByTimeRequestDTO input) {
        return locationService.getListByTime(input);
    }
}
