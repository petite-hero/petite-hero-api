package capstone.petitehero.services;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.ResponseObject;
import capstone.petitehero.dtos.request.location.AddLocationRequestDTO;
import capstone.petitehero.dtos.request.location.GetListByTimeRequestDTO;
import capstone.petitehero.dtos.response.location.GetListByTimeResponseDTO;
import capstone.petitehero.entities.Child;
import capstone.petitehero.entities.LocationHistory;
import capstone.petitehero.repositories.ChildRepository;
import capstone.petitehero.repositories.LocationRepository;
import capstone.petitehero.utilities.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ChildRepository childRepository;

    public ResponseObject recordLocationFromSW (AddLocationRequestDTO sentLocation) {
        ResponseObject result = Util.createResponse();

        Child child = childRepository.getOne(sentLocation.getChild());

        LocationHistory addedLocation = new LocationHistory();
        addedLocation.setXCoordinate(sentLocation.getLatitude());
        addedLocation.setYCoordinate(sentLocation.getLongitude());
        addedLocation.setTime(new Date(sentLocation.getTime()));
        addedLocation.setStatus(sentLocation.getStatus());
        addedLocation.setChild(child);

        LocationHistory location = locationRepository.save(addedLocation);

        if (location == null) {
            result.setData(null);
            result.setErrorMsg("Bad request - No data provided");
            result.setCode(Constants.CODE_400);
        } else {
            result.setData(sentLocation);
            result.setMsg("Added successfully!");
        }
        return result;
    }

    public ResponseObject getListByTime(GetListByTimeRequestDTO input) {
        ResponseObject result = Util.createResponse();

        Long timeCriteria = System.currentTimeMillis() - (input.getTime() * Constants.ONE_HOUR_IN_MILLISECOND);
        
        Child child = childRepository.getOne(input.getChild());

        LocationHistory criteriaLocation = new LocationHistory();
        criteriaLocation.setChild(child);
        Example<LocationHistory> criteria = Example.of(criteriaLocation);

        List<LocationHistory> rawData = locationRepository.findAll(criteria);
        List<GetListByTimeResponseDTO> filteredData = new ArrayList<>();


        System.out.println("time criteria: " + new Date(timeCriteria));
        for (LocationHistory location : rawData) {

            if (location.getTime().getTime() > timeCriteria) {
                System.out.println("obj time: " + location.getTime());
                GetListByTimeResponseDTO temp = new GetListByTimeResponseDTO();
                temp.setLatitude(location.getXCoordinate());
                temp.setLongitude(location.getYCoordinate());
                temp.setStatus(location.getStatus());
                temp.setTime(location.getTime().getTime());
                filteredData.add(temp);
            }
        }
        result.setData(filteredData);
        result.setMsg("Get data successfully!");
        return result;
    }
}
