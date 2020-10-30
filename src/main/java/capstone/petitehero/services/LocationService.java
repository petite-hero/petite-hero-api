package capstone.petitehero.services;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.ParentChildPushTokenDTO;
import capstone.petitehero.dtos.ResponseObject;
import capstone.petitehero.dtos.request.location.AddLocationRequestDTO;
import capstone.petitehero.dtos.request.location.PushSilentNotiSWDTO;
import capstone.petitehero.dtos.response.location.GetLastestLocationResponseDTO;
import capstone.petitehero.dtos.response.location.GetListByTimeResponseDTO;
import capstone.petitehero.entities.Child;
import capstone.petitehero.entities.LocationHistory;
import capstone.petitehero.repositories.ChildRepository;
import capstone.petitehero.repositories.LocationRepository;
import capstone.petitehero.utilities.Util;
import com.google.gson.Gson;
import io.swagger.models.auth.In;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import sun.net.www.protocol.http.HttpURLConnection;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ChildRepository childRepository;


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
                    ArrayList<String> tokens = locationRepository.getParentPushToken(sentLocation.getChild());
                    Integer pushStatus = 100;
                    if (emergency) { // in case mobile device demands emergency mode
                        pushStatus = pushSilentNotificationMobile(sentLocation, tokens);
                    } else { // in case mobile device doesn't demand emergency mode
                        if (location.getStatus() != latestLocation.getStatus()) { // notify mobile if child' status changes
                            String msg = location.getStatus() ? Constants.CHILD_SAFE : Constants.CHILD_NOT_SAFE;
                            pushStatus = pushNotificationMobile(msg, sentLocation, tokens);
                        }
                    }

                    if (pushStatus == Constants.CODE_200) {
                        result.setMsg(Constants.NO_ERROR);
                    } else if (pushStatus == Constants.CODE_500) {
                        result.setMsg("Error at recordLocationFromSW - pushNotification");
                    }
                    result.setData(sentLocation);
                }
            }
        } catch (Exception e) {
            result.setData(null);
            result.setMsg(Constants.SERVER_ERROR + e.toString());
            result.setCode(Constants.CODE_500);
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
                PushSilentNotiSWDTO data = new PushSilentNotiSWDTO();
                if (emergency) {
                    data.setTitle(Constants.EMERGENCY);
                } else {
                    data.setTitle(Constants.STOP_EMERGENCY);
                }
                getLatestChildLocation(childId);
                System.out.println("====> Child token: " + child.getPushToken());
                Integer pushStatus = pushSilentNotificationSW(data, child.getPushToken());
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
                PushSilentNotiSWDTO data = new PushSilentNotiSWDTO();
                if (status) {
                    data.setTitle(Constants.TRACKING_ACTIVE);
                } else {
                    data.setTitle(Constants.TRACKING_INACTIVE);
                }
                getLatestChildLocation(childId);
                System.out.println("====> Child token: " + child.getPushToken());
                Integer pushStatus = pushSilentNotificationSW(data, child.getPushToken());
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

    public Integer pushSilentNotificationMobile(Object data, ArrayList<String> pushTokens) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        Integer result;
        try {
            HttpPost request = new HttpPost(Constants.EXPO_PUSH_NOTI_URL);
            HashMap<String, Object> body = new HashMap<String, Object>();
            body.put("title", Constants.SILENT_NOTI);
            body.put("data", new Gson().toJson(data));
            body.put("to", pushTokens);
            StringEntity bodyJson = new StringEntity(new Gson().toJson(body));

            // headers specified by Expo to request push notifications
            request.setHeader(HttpHeaders.HOST, "exp.host");
            request.setHeader(HttpHeaders.ACCEPT, "application/json");
            request.setHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate");
            request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            request.setEntity(bodyJson);

            // get response
            HttpResponse response = httpClient.execute(request);

            // handle response here...
            result = response.getStatusLine().getStatusCode();

        } catch (Exception ex) {
            result = Constants.CODE_500;
            System.out.println(Constants.SERVER_ERROR + ex.toString());
            ex.printStackTrace();
        }
        return result;
    }

    public Integer pushSilentNotificationSW(Object data, String pushToken) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        Integer result;
        try {
            HttpPost request = new HttpPost(Constants.FCM_PUSH_NOTI_URL);
            HashMap<String, Object> body = new HashMap<String, Object>();
            body.put("data", data);
            body.put("to", pushToken);
            StringEntity bodyJson = new StringEntity(new Gson().toJson(body));

            System.out.println("===> Body sent: " + new Gson().toJson(body));

            // headers specified by FCM to request push notifications
            request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            request.setHeader(HttpHeaders.AUTHORIZATION, Constants.FCM_SERVER_KEY);
            request.setEntity(bodyJson);

            // get response
            HttpResponse response = httpClient.execute(request);

            // handle response here...
            result = response.getStatusLine().getStatusCode();
            System.out.println(result);
        } catch (Exception ex) {
            result = Constants.CODE_500;
            System.out.println(Constants.SERVER_ERROR + ex.toString());
            ex.printStackTrace();
        }
        return result;
    }

    public Integer pushNotificationMobile(String msg, Object data, ArrayList<String> pushTokens) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        Integer result;
        try {
            HttpPost request = new HttpPost(Constants.EXPO_PUSH_NOTI_URL);
            HashMap<String, Object> body = new HashMap<String, Object>();
            body.put("title", Constants.PETITE_HERO);
            body.put("body", msg);
            body.put("data", new Gson().toJson(data));
            body.put("to", pushTokens);
            StringEntity bodyJson = new StringEntity(new Gson().toJson(body));

            // headers specified by Expo to request push notifications
            request.setHeader(HttpHeaders.HOST, "exp.host");
            request.setHeader(HttpHeaders.ACCEPT, "application/json");
            request.setHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate");
            request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            request.setEntity(bodyJson);

            // get response
            HttpResponse response = httpClient.execute(request);

            // handle response here...
            result = response.getStatusLine().getStatusCode();
            System.out.println(result);

        } catch (Exception ex) {
            result = Constants.CODE_500;
            System.out.println(Constants.SERVER_ERROR + ex.toString());
            ex.printStackTrace();
        }
        return result;
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
//    public static void main(String[] args) {
//        PushSilentNotiSWDTO obj = new PushSilentNotiSWDTO("emergency", "This is body", null);
//        PushSilentNotiSWDTO obj = new PushSilentNotiSWDTO("stop_emergency", "This is body", null);
//        pushSilentNotificationSW(obj,"fHyv-M43R2mFkEeAFB4Y0V:APA91bErIx0LNb5EHDIasKlu2Or_3ZZ6z9buxB85MGTTmCRiGFgZR-c0JKk5F58T81B9xYxWDB6VhsF_mzrxcHq8J_ru9kJzWf0ARgZnBAD0r3aP483aZtPecblYODl_6JDnDBRLpEWf");
//}
}
