package capstone.petitehero.services;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.ResponseObject;
import capstone.petitehero.dtos.request.location.AddLocationRequestDTO;
import capstone.petitehero.dtos.response.location.GetLastestLocationResponseDTO;
import capstone.petitehero.dtos.response.location.GetListByTimeResponseDTO;
import capstone.petitehero.entities.Child;
import capstone.petitehero.entities.LocationHistory;
import capstone.petitehero.repositories.ChildRepository;
import capstone.petitehero.repositories.LocationRepository;
import capstone.petitehero.utilities.Util;
import com.google.gson.Gson;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ChildRepository childRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public ResponseObject recordLocationFromSW (AddLocationRequestDTO sentLocation) {
        ResponseObject result = Util.createResponse();

        Child child = childRepository.getOne(sentLocation.getChild());

        LocationHistory addedLocation = new LocationHistory();
        addedLocation.setLatitude(sentLocation.getLatitude());
        addedLocation.setLongitude(sentLocation.getLongitude());
        addedLocation.setTime(sentLocation.getTime());
        addedLocation.setStatus(sentLocation.getStatus());
        addedLocation.setChild(child);

        LocationHistory location = locationRepository.save(addedLocation);

        if (location == null) {
            result.setData(null);
            result.setMsg("Bad request - No data provided");
            result.setCode(Constants.CODE_400);
        } else {
            result.setData(sentLocation);
            result.setMsg("Added successfully!");
        }
        return result;
    }

    public ResponseObject getListByTime(Long childId, Long from, Long to) {
        ResponseObject result = Util.createResponse();

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
        result.setMsg("Get data successfully!");
        return result;
    }

    public ResponseObject getLatestChildLocation(Long childId) {
        ResponseObject result = Util.createResponse();
        Child child = childRepository.getOne(childId);

        if (child == null) {
            result.setMsg("Bad Request - Child ID doesn't exist");
            result.setCode(Constants.CODE_400);
            return result;
        }

        LocationHistory location = locationRepository.findLastestLocation(childId);
        GetLastestLocationResponseDTO latestLocation = new GetLastestLocationResponseDTO();
        latestLocation.setLatitude(location.getLatitude());
        latestLocation.setLongitude(location.getLongitude());
        latestLocation.setStatus(location.getStatus());
        result.setData(latestLocation);
        result.setMsg("Get data successfully!");
        return result;
    }


    public void pushNotifications(String titleMsg, String bodyMsg) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        try {
            HttpPost request = new HttpPost(Constants.EXPO_PUSH_NOTI_URL);

            HashMap<String, Object> body = new HashMap<String, Object>();

//            body.put("title", titleMsg);
//            body.put("body", bodyMsg);
            body.put("sound", "default");
			body.put("data", "{\"name\":\"Enri\"}");
            // body.put("subtitle", "This is Subtitle message");
            // body.put("badge", "1"); // this indicates the number of notification number on your application icon

//            ArrayList<String> pushToTokens = new ArrayList<String>();
//            for (HashMap.Entry<Long, String> entry : pushTokens.entrySet()) {
////				if (!pushToTokens.contains(entry.getValue())) {
//                pushToTokens.add(entry.getValue());
////				}
//            }
            body.put("to", "ExponentPushToken[te1ST8Mh6fwgf_AlThdTl3]");
            StringEntity bodyJson = new StringEntity(new Gson().toJson(body));

            // headers specified by Expo to request push notifications
            request.setHeader(HttpHeaders.HOST, "exp.host");
            request.setHeader(HttpHeaders.ACCEPT, "application/json");
            request.setHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate");
            request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            request.setEntity(bodyJson);

            HttpResponse response = httpClient.execute(request);
            // handle response here...
            System.out.println(response.getStatusLine());

        } catch (Exception ex) {
            System.out.println("===> Error at Push Notification API");
            ex.printStackTrace();
        }
    }

////    public void pushNotifications(String titleMsg, String bodyMsg, HashMap<Long, String> pushTokens) {
////        for (HashMap.Entry<Long, String> entry : pushTokens.entrySet()) {
////            Notification noti = new Notification();
////            noti.setUserId(entry.getKey());
////            noti.setDetails(titleMsg + "\n" + bodyMsg);
////            noti.setCreatedAt(Utility.getSystemCurrentMilli());
////            noti.setIsRead(Constant.IS_NOT_DELETED);
////            notificationRepository.save(noti);
////        }
//    public void pushNotifications(String titleMsg, String bodyMsg) {
//        HttpClient httpClient = HttpClientBuilder.create().build();
//        try {
//            HttpPost request = new HttpPost(Constants.EXPO_PUSH_NOTI_URL);
//
//            HashMap<String, Object> body = new HashMap<String, Object>();
//
//            body.put("title", titleMsg);
//            body.put("body", bodyMsg);
//            body.put("sound", "default");
////			body.put("data", "{\"name\":\"Enri\"}");
//            // body.put("subtitle", "This is Subtitle message");
//            // body.put("badge", "1"); // this indicates the number of notification number
//            // on your application icon
//
////            ArrayList<String> pushToTokens = new ArrayList<String>();
////            for (HashMap.Entry<Long, String> entry : pushTokens.entrySet()) {
//////				if (!pushToTokens.contains(entry.getValue())) {
////                pushToTokens.add(entry.getValue());
//////				}
////            }
//            body.put("to", "ExponentPushToken[te1ST8Mh6fwgf_AlThdTl3]");
////            System.out.println(pushToTokens);
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

// getListByTime
//        Long timeCriteria = System.currentTimeMillis() - (time * Constants.ONE_HOUR_IN_MILLISECOND);
//        Child child = childRepository.getOne(childId);
//        LocationHistory criteriaLocation = new LocationHistory();
//        criteriaLocation.setChild(child);
//        Example<LocationHistory> criteria = Example.of(criteriaLocation);
//        List<LocationHistory> rawData = locationRepository.findAll(criteria);
//        List<GetListByTimeResponseDTO> filteredData = new ArrayList<>()
//        System.out.println("time criteria: " + new Date(timeCriteria));
//        for (LocationHistory location : rawData) {
//            if (location.getTime() >= from && location.getTime() <= to) {
//                System.out.println("obj time: " + location.getTime());
//                GetListByTimeResponseDTO temp = new GetListByTimeResponseDTO();
//                temp.setLatitude(location.getLatitude());
//                temp.setLongitude(location.getLongitude());
//                temp.setStatus(location.getStatus());
//                temp.setTime(location.getTime());
//                filteredData.add(temp);
//            }
//        }
}
