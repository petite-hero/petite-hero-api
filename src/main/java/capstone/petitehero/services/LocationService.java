package capstone.petitehero.services;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.ResponseObject;
import capstone.petitehero.dtos.request.location.AddLocationRequestDTO;
import capstone.petitehero.dtos.request.location.PushNotiSWDTO;
import capstone.petitehero.dtos.response.location.GetLastestLocationResponseDTO;
import capstone.petitehero.dtos.response.location.GetListByTimeResponseDTO;
import capstone.petitehero.entities.Child;
import capstone.petitehero.entities.LocationHistory;
import capstone.petitehero.repositories.ChildRepository;
import capstone.petitehero.repositories.LocationRepository;
import capstone.petitehero.utilities.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private NotificationService notiService;

    public ResponseObject recordLocationFromSW (AddLocationRequestDTO sentLocation, Boolean emergency) {
        ResponseObject result = Util.createResponse();
        try {
            Child child = childRepository.getOne(sentLocation.getChild());
            System.out.println("===> ChildID: " + sentLocation.getChild());
            if (child == null) {
                result.setData(null);
                result.setMsg("Bad request - Child doesn't exist");
                result.setCode(Constants.CODE_400);
            } else {
                LocationHistory latestLocation = locationRepository.findLatestLocation(child.getChildId());
                if (latestLocation == null) { // in case child doesn't have any location history yet
                    latestLocation = new LocationHistory();
                    latestLocation.setStatus(Constants.NOT_SAFE);
                }
                LocationHistory addedLocation = new LocationHistory();
                addedLocation.setLatitude(sentLocation.getLatitude());
                addedLocation.setLongitude(sentLocation.getLongitude());
                addedLocation.setTime(sentLocation.getTime());
                addedLocation.setStatus(sentLocation.getStatus());
                addedLocation.setProvider(sentLocation.getProvider());
                addedLocation.setChild(child);
                LocationHistory location = locationRepository.save(addedLocation);

                if (location == null) {
                    result.setData(null);
                    result.setMsg("Bad request - No data provided");
                    result.setCode(Constants.CODE_400);
                } else {
                    result.setMsg(Constants.NO_ERROR);

                    ArrayList<String> tokens = locationRepository.getParentPushToken(sentLocation.getChild());
                    if (tokens == null || tokens.size() == 0) {
                        tokens = new ArrayList<>();
                        tokens.add(Constants.FAKE_EXPO_TOKEN);
                        result.setMsg("Parent token is currently null");
                    }
                    Integer pushStatus = 100;
                    if (emergency) { // in case mobile device demands emergency mode
//                        pushStatus = notiService.pushNotificationMobile(null, sentLocation, tokens);
                        if (tokens.size() == 1) {
                            pushStatus = notiService.pushNotificationMobile(null, sentLocation, tokens);
                        } else {
                            ArrayList<String> pushToken;
                            for (String token : tokens) {
                                pushToken = new ArrayList<>();
                                pushToken.add(token);
                                pushStatus = notiService.pushNotificationMobile(null, sentLocation, pushToken);
                            }
                        }
                    } else { // in case mobile device doesn't demand emergency mode
                        if (location.getStatus() != latestLocation.getStatus()) { // notify mobile if child' status changes
                            String msg = location.getStatus() ? Constants.CHILD_SAFE : Constants.CHILD_NOT_SAFE;
                            if (tokens.size() == 1) {
                                pushStatus = notiService.pushNotificationMobile(msg, sentLocation, tokens);
                            } else {
                                ArrayList<String> pushToken;
                                for (String token : tokens) {
                                    pushToken = new ArrayList<>();
                                    pushToken.add(token);
                                    pushStatus = notiService.pushNotificationMobile(msg, sentLocation, pushToken);
                                }
                            }
                        }
                    }

                    if (pushStatus == Constants.CODE_500) {
                        result.setMsg("Error at recordLocationFromSW - pushNotification");
                    }
                    result.setData(sentLocation);
                }
            }
        } catch (Exception e) {
            result.setData(null);
            result.setMsg(Constants.SERVER_ERROR + e.toString());
            result.setCode(Constants.CODE_500);
            e.printStackTrace();
        }
        return result;
    }

    public ResponseObject recordListLocationFromSW (List<AddLocationRequestDTO> sentLocations) {
        ResponseObject result = Util.createResponse();
        try {
            if (sentLocations.size() > 0) {
                Child child = childRepository.getOne(sentLocations.get(0).getChild());
                System.out.println("===> ChildID: " + sentLocations.get(0).getChild());

                if (child == null) {
                    result.setData(null);
                    result.setMsg("Bad request - Child doesn't exist");
                    result.setCode(Constants.CODE_400);
                } else {
                    LocationHistory addedLoc = new LocationHistory();
                    Iterable<LocationHistory> addedList = new ArrayList<>();
                    for (AddLocationRequestDTO loc : sentLocations) {
                        addedLoc.setLatitude(loc.getLatitude());
                        addedLoc.setLongitude(loc.getLongitude());
                        addedLoc.setTime(loc.getTime());
                        addedLoc.setStatus(loc.getStatus());
                        addedLoc.setProvider(loc.getProvider());
                        addedLoc.setChild(child);
                        ((ArrayList<LocationHistory>) addedList).add(addedLoc);
                    }
                    Iterable<LocationHistory> list = locationRepository.saveAll(addedList);
                    if (list == null) {
                        result.setData(null);
                        result.setMsg("ERROR - ERROR occurred while adding");
                        result.setCode(Constants.CODE_500);
                    } else {
                        result.setMsg(Constants.NO_ERROR);
                        result.setData(sentLocations);
                    }
                }
            } else {
                result.setData(null);
                result.setMsg("Bad request - No data provided");
                result.setCode(Constants.CODE_400);
            }
        } catch (Exception e) {
            result.setData(null);
            result.setMsg(Constants.SERVER_ERROR + e.toString());
            result.setCode(Constants.CODE_500);
            e.printStackTrace();
        }
        return result;
    }



    public ResponseObject getListByTime(Long childId, Long from, Long to) {
        ResponseObject result = Util.createResponse();
        try {
            List<LocationHistory> rawData = locationRepository.getListByTime(childId, from, to);
            List<GetListByTimeResponseDTO> filteredData = new ArrayList<>();

            for (LocationHistory location : rawData) {
                GetListByTimeResponseDTO temp = new GetListByTimeResponseDTO();
                temp.setLatitude(location.getLatitude());
                temp.setLongitude(location.getLongitude());
                temp.setStatus(location.getStatus());
                temp.setTime(location.getTime());
                filteredData.add(temp);
            }

            result.setData(filteredData);
            result.setMsg(Constants.GET_DATA_SUCCESSFULLY);
        } catch (Exception e) {
            result.setData(null);
            result.setMsg(Constants.SERVER_ERROR + e.toString());
            result.setCode(Constants.CODE_500);
        }
        return result;
    }

    public ResponseObject getLatestChildLocation(Long childId) {
        ResponseObject result = Util.createResponse();
        try {
            Child child = childRepository.getOne(childId);
            System.out.println("===> ChildID: " + childId);
            if (child == null) {
                result.setMsg("Bad Request - Child ID doesn't exist");
                result.setCode(Constants.CODE_400);
                return result;
            }

            LocationHistory location = locationRepository.findLatestLocation(childId);
            if (location != null) {
                GetLastestLocationResponseDTO latestLocation = new GetLastestLocationResponseDTO();
                latestLocation.setLatitude(location.getLatitude());
                latestLocation.setLongitude(location.getLongitude());
                latestLocation.setStatus(location.getStatus());
                result.setData(latestLocation);
                result.setMsg(Constants.GET_DATA_SUCCESSFULLY);
            } else {
                result.setData(null);
                result.setMsg("Child doesn't have any location history yet");
            }
        } catch (Exception e) {
            result.setData(null);
            result.setMsg(Constants.SERVER_ERROR + e.toString());
            result.setCode(Constants.CODE_500);
        }
        return result;
    }

    public ResponseObject updateEmergencyStatus(Long childId, Boolean emergency) {
        ResponseObject result = Util.createResponse();
        try {
            Child child = childRepository.getOne(childId);
            if (child == null) {
                result.setMsg("Bad Request - Child ID doesn't exist");
                result.setCode(Constants.CODE_400);
            } else {
                PushNotiSWDTO data = new PushNotiSWDTO(Constants.SILENT_NOTI, null, null);
                if (emergency) {
                    data.setBody(Constants.EMERGENCY);
                } else {
                    data.setBody(Constants.STOP_EMERGENCY);
                }
                getLatestChildLocation(childId);
                System.out.println("====> Child token: " + child.getPushToken());
                Integer pushStatus = notiService.pushNotificationSW(data, child.getPushToken());
                if (pushStatus == Constants.CODE_200) {
                    result.setMsg("Update emergency successfully!");
                } else if (pushStatus == Constants.CODE_500) {
                    result.setMsg("Error at pushSilentNotificationSW");
                }
            }
        } catch (Exception e) {
            result.setData(null);
            result.setMsg(Constants.SERVER_ERROR + e.toString());
            result.setCode(Constants.CODE_500);
        }
        return result;
    }

    public ResponseObject changeSWStatus(Long childId, Boolean status) {
        ResponseObject result = Util.createResponse();
        try {
            Child child = childRepository.getOne(childId);
            if (child == null) {
                result.setMsg("Bad Request - Child ID doesn't exist");
                result.setCode(Constants.CODE_400);
            } else {
                child.setTrackingActive(status);
                childRepository.save(child);

                PushNotiSWDTO data = new PushNotiSWDTO(Constants.SILENT_NOTI, null, null);
                if (status) {
                    data.setBody(Constants.TRACKING_ACTIVE);
                } else {
                    data.setBody(Constants.TRACKING_INACTIVE);
                }
                System.out.println("====> Child token: " + child.getPushToken());
                Integer pushStatus = notiService.pushNotificationSW(data, child.getPushToken());
                if (pushStatus == Constants.CODE_200) {
                    result.setMsg("Change status successfully!");
                } else if (pushStatus == Constants.CODE_500) {
                    result.setMsg("Error at pushSilentNotificationSW");
                }
            }
        } catch (Exception e) {
            result.setData(null);
            result.setMsg(Constants.SERVER_ERROR + e.toString());
            result.setCode(Constants.CODE_500);
        }
        return result;
    }
}
