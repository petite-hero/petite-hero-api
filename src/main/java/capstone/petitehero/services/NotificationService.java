package capstone.petitehero.services;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.request.location.PushNotiSWDTO;
import capstone.petitehero.utilities.Util;
import com.google.gson.Gson;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;

@Service
public class NotificationService {

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
            System.out.println(result);

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

    public Integer notifySWSafeZoneChanges(String pushToken, String repeatOn) {
        Integer pushStatus = 100;
        try {
            if (Util.fromRepeatOnStringToDayInWeek(repeatOn).contains(Util.getCurrentWeekday())) {
                PushNotiSWDTO data = new PushNotiSWDTO("Updated Safe Zone", null, null);
                pushStatus = pushSilentNotificationSW(data, pushToken);
            } else {
                pushStatus = Constants.CODE_200;
            }
        } catch (Exception e) {
            System.out.println("Error at notifySWSafeZoneChanges: " + e.toString());
            e.printStackTrace();
        }
        return pushStatus;
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
//    public void pushSWNotification() {
//        try {
//            FileInputStream serviceAccount =
//                    new FileInputStream("pure-display-290409-firebase-adminsdk-rhjpl-af1073d6dc.json");
//
//            FirebaseOptions options = new FirebaseOptions.Builder()
//                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                    .setDatabaseUrl("https://pure-display-290409.firebaseio.com")
//                    .build();
//
//            FirebaseApp.initializeApp(options);
//        } catch (Exception e) {
//
//        }
//
//    }
//    public static void main(String[] args) {
//        PushNotiSWDTO obj = new PushNotiSWDTO("emergency", "This is body", null);
//        PushNotiSWDTO obj = new PushNotiSWDTO("stop_emergency", "This is body", null);
//        pushSilentNotificationSW(obj,"fHyv-M43R2mFkEeAFB4Y0V:APA91bErIx0LNb5EHDIasKlu2Or_3ZZ6z9buxB85MGTTmCRiGFgZR-c0JKk5F58T81B9xYxWDB6VhsF_mzrxcHq8J_ru9kJzWf0ARgZnBAD0r3aP483aZtPecblYODl_6JDnDBRLpEWf");
//}
}
