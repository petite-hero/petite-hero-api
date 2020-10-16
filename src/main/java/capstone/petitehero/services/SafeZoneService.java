package capstone.petitehero.services;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.ResponseObject;
import capstone.petitehero.dtos.request.location.AddLocationRequestDTO;
import capstone.petitehero.dtos.request.location.AddNewSafeZoneRequestDTO;
import capstone.petitehero.dtos.response.location.GetLastestLocationResponseDTO;
import capstone.petitehero.dtos.response.location.GetListByDateResponseDTO;
import capstone.petitehero.dtos.response.location.GetListByTimeResponseDTO;
import capstone.petitehero.dtos.response.location.GetSafeZoneDetailResponseDTO;
import capstone.petitehero.entities.Child;
import capstone.petitehero.entities.LocationHistory;
import capstone.petitehero.entities.Parent;
import capstone.petitehero.entities.Safezone;
import capstone.petitehero.repositories.ChildRepository;
import capstone.petitehero.repositories.LocationRepository;
import capstone.petitehero.repositories.ParentRepository;
import capstone.petitehero.repositories.SafeZoneRepository;
import capstone.petitehero.utilities.Util;
import com.google.gson.Gson;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class SafeZoneService {

    @Autowired
    private SafeZoneRepository safeZoneRepository;

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private ParentRepository parentRepository;

    public ResponseObject addSafeZone(AddNewSafeZoneRequestDTO sentSafeZone) {
        ResponseObject result = Util.createResponse();
        try {
            Child child = childRepository.getOne(sentSafeZone.getChildId());
            Parent parent = parentRepository.getOne(sentSafeZone.getCreatorId());

            if (child == null || parent == null) {
                result.setData(null);
                result.setMsg("Bad request - Child or Parent doesn't exist");
                result.setCode(Constants.CODE_400);
            } else {
                Safezone addedSafeZone = new Safezone();
                addedSafeZone.setName(sentSafeZone.getName());
                addedSafeZone.setLatitude(sentSafeZone.getLatitude());
                addedSafeZone.setLongitude(sentSafeZone.getLongitude());
                addedSafeZone.setFromTime(sentSafeZone.getFromTime());
                addedSafeZone.setToTime(sentSafeZone.getToTime());
                addedSafeZone.setDate(sentSafeZone.getDate());
                addedSafeZone.setRepeatOn(sentSafeZone.getRepeatOn());
                addedSafeZone.setRadius(sentSafeZone.getRadius());
                addedSafeZone.setIsDisabled(false);
                addedSafeZone.setType(sentSafeZone.getType());
                addedSafeZone.setChild(child);
                addedSafeZone.setParent(parent);

                Safezone safezone = safeZoneRepository.save(addedSafeZone);

                if (safezone != null) {
                    result.setData(sentSafeZone);
                    result.setMsg("Added successfully!");
                } else {
                    result.setData(null);
                    result.setMsg("Error occurred while adding safe zone");
                    result.setCode(Constants.CODE_500);
                }
            }
        } catch (Exception e) {
            result.setData(null);
            result.setMsg("Server Error: " + e.toString());
            result.setCode(Constants.CODE_500);
        }
        return  result;
    }

    public ResponseObject getListByDate(Long childId, Long date) {
        ResponseObject result = Util.createResponse();
        try {
            Child child = childRepository.getOne(childId);
            if (child == null) {
                result.setData(null);
                result.setMsg("Bad request - Child doesn't exist");
                result.setCode(Constants.CODE_400);
            } else {
                List<Safezone> rawData = safeZoneRepository.getListByDate(childId, date);
                List<GetListByDateResponseDTO> filteredData = new ArrayList<>();
                GetListByDateResponseDTO temp;
                for (Safezone safezone : rawData) {
                    temp = new GetListByDateResponseDTO(safezone.getSafezoneId(), safezone.getName(), safezone.getLatitude(), safezone.getLongitude(), safezone.getFromTime(), safezone.getToTime(), safezone.getType());
                    filteredData.add(temp);
                }
                result.setData(filteredData);
                result.setMsg(Constants.NO_ERROR);
            }
        } catch (Exception e) {
            result.setData(null);
            result.setMsg("Server Error: " + e.toString());
            result.setCode(Constants.CODE_500);
        }
        return result;
    }

    public ResponseObject getSafeZoneDetail(Long safezoneId) {
        ResponseObject result = Util.createResponse();
        try {
            Safezone safezone = safeZoneRepository.getOne(safezoneId);
            if (safezone == null) {
                result.setData(null);
                result.setMsg("Bad request - SafeZone doesn't exist");
                result.setCode(Constants.CODE_400);
            } else {
                GetSafeZoneDetailResponseDTO response = new GetSafeZoneDetailResponseDTO(
                        safezone.getSafezoneId(),
                        safezone.getName(),
                        safezone.getLatitude(),
                        safezone.getLongitude(),
                        safezone.getFromTime(),
                        safezone.getToTime(),
                        safezone.getDate(),
                        safezone.getRepeatOn(),
                        safezone.getRadius(),
                        safezone.getIsDisabled(),
                        safezone.getType(),
                        safezone.getChild().getChildId(),
                        safezone.getParent().getId()
                );
                result.setData(response);
                result.setMsg(Constants.NO_ERROR);
            }
        } catch (Exception e) {
            result.setData(null);
            result.setMsg("Server Error: " + e.toString());
            result.setCode(Constants.CODE_500);
        }
        return  result;
    }



//    public ResponseObject getListByTime(Long childId, Long from, Long to) {
//        ResponseObject result = Util.createResponse();
//
//        List<LocationHistory> rawData = locationRepository.getListByTime(childId, from, to);
//        List<GetListByTimeResponseDTO> filteredData = new ArrayList<>();
//
//        for (LocationHistory location : rawData) {
//            GetListByTimeResponseDTO temp = new GetListByTimeResponseDTO();
//            temp.setLatitude(location.getLatitude());
//            temp.setLongitude(location.getLongitude());
//            temp.setStatus(location.getStatus());
//            temp.setTime(location.getTime());
//            filteredData.add(temp);
//        }
//
//        result.setData(filteredData);
//        result.setMsg("Get data successfully!");
//        return result;
//    }
//
//    public ResponseObject getLatestChildLocation(Long childId) {
//        ResponseObject result = Util.createResponse();
//        Child child = childRepository.getOne(childId);
//
//        if (child == null) {
//            result.setMsg("Bad Request - Child ID doesn't exist");
//            result.setCode(Constants.CODE_400);
//            return result;
//        }
//
//        LocationHistory location = locationRepository.findLastestLocation(childId);
//        GetLastestLocationResponseDTO latestLocation = new GetLastestLocationResponseDTO();
//        latestLocation.setLatitude(location.getLatitude());
//        latestLocation.setLongitude(location.getLongitude());
//        latestLocation.setStatus(location.getStatus());
//        result.setData(latestLocation);
//        result.setMsg("Get data successfully!");
//        return result;
//    }
//
//
//    public void pushNotifications(String titleMsg, String bodyMsg) {
//        HttpClient httpClient = HttpClientBuilder.create().build();
//        try {
//            HttpPost request = new HttpPost(Constants.EXPO_PUSH_NOTI_URL);
//
//            HashMap<String, Object> body = new HashMap<String, Object>();
//
////            body.put("title", titleMsg);
////            body.put("body", bodyMsg);
//            body.put("sound", "default");
//			body.put("data", "{\"name\":\"Enri\"}");
//            // body.put("subtitle", "This is Subtitle message");
//            // body.put("badge", "1"); // this indicates the number of notification number on your application icon
//
////            ArrayList<String> pushToTokens = new ArrayList<String>();
////            for (HashMap.Entry<Long, String> entry : pushTokens.entrySet()) {
//////				if (!pushToTokens.contains(entry.getValue())) {
////                pushToTokens.add(entry.getValue());
//////				}
////            }
//            body.put("to", "ExponentPushToken[te1ST8Mh6fwgf_AlThdTl3]");
//            StringEntity bodyJson = new StringEntity(new Gson().toJson(body));
//
//            // headers specified by Expo to request push notifications
//            request.setHeader(HttpHeaders.HOST, "exp.host");
//            request.setHeader(HttpHeaders.ACCEPT, "application/json");
//            request.setHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate");
//            request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
//            request.setEntity(bodyJson);
//
//            HttpResponse response = httpClient.execute(request);
//            // handle response here...
//            System.out.println(response.getStatusLine());
//
//        } catch (Exception ex) {
//            System.out.println("===> Error at Push Notification API");
//            ex.printStackTrace();
//        }
//    }
}
