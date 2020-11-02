package capstone.petitehero.utilities;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.ResponseObject;
import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class Util {

    public boolean validatePhoneNumberParent(String phoneNumber) {
        // 1234567890
        // 123-456-7890
        // 123 456 7890
        return phoneNumber.matches("(\\d{3}(\\s|-)?\\d{3,4}(\\s|-)?\\d{3,4})");
    }

    public static ResponseObject createResponse() {
        ResponseObject result = new ResponseObject();
        result.setCode(Constants.CODE_200);
        result.setMsg(Constants.NO_ERROR);
        result.setData(null);
        return result;
    }

    public boolean validateEmail(String email) {
        return email.matches("^[a-zA-Z0-9]+@([a-zA-Z]{2,6}\\.)+[a-zA-Z]{2,6}$");
    }

    public static String fromImageFileToBase64String(String photoFileName) {
        try {
            File imageLocation = new File(Constants.UPLOAD_FOLDER + "/" + photoFileName);

            byte[] bytesPhoto = FileUtils.readFileToByteArray(imageLocation);

            return Base64.getEncoder().encodeToString(bytesPhoto);
        } catch (Exception e) {
            return "Cannot get image";
        }
    }

    public static String fromBadgeImageFileToBase64String(String questBadge) {
        try {
            File imageLocation = new File(Constants.BADGE_FOLDER + "/" + questBadge);

            byte[] bytesPhoto = FileUtils.readFileToByteArray(imageLocation);

            return Base64.getEncoder().encodeToString(bytesPhoto);
        } catch (Exception e) {
            return "Cannot get badge image";
        }
    }

    public static String saveImageToSystem(String idImage, String content, MultipartFile photo) {
        File f = new File(Constants.UPLOAD_FOLDER);

        // photo name
        String fileName = idImage + "-"
                + new Date().getTime() + "-"
                + content + ".png";
        if (!f.exists()) {
            f.mkdir();
        }
        try {

            Path path = Paths.get(Constants.UPLOAD_FOLDER + fileName);
            byte[] bytesPhoto = photo.getBytes();
            Files.write(path, bytesPhoto);

            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean validatePasswordForAllAccount(String password) {
        return password.matches("[a-zA-Z0-9]{6,8}");
    }

    public static String formatTimestampToDateTime(Long timeStamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return sdf.format(new Date(timeStamp));
    }

    public static String formatTimestampToTime(Long timeStamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date(timeStamp));
    }

    // for filter list to distinct list
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public static Long getStartDay(Long timeStampDate){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timeStampDate));
        calendar.set(Calendar.AM_PM, Calendar.AM);

        // set hour, minutes, seconds, milliseconds at start date
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    public static Long getEndDay(Long timeStampDate){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timeStampDate));
        calendar.set(Calendar.AM_PM, Calendar.PM);

        // set hour, minutes, seconds, milliseconds at end date
        calendar.set(Calendar.HOUR, 11);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return calendar.getTimeInMillis();
    }

    // this method will return an int
    // return value is to get the index of day need to query
    // in the string repeat on of task or safe zone
    public static int fromTimeStampToDayInWeek(Long timeStampDayRepeat) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timeStampDayRepeat));
        int dayInWeek = calendar.get(Calendar.DAY_OF_WEEK);
        // Calendar.DAY_OF_WEEK will return int
        // SunDay - Saturday will be 1 - 7 (1 is Sunday and 7 is Saturday)
        if (dayInWeek == 1) { // Sunday
            return 6;
        } else if (dayInWeek == 2) { // Monday
            return 0;
        } else if (dayInWeek == 3) { // Tuesday
            return 1;
        } else if (dayInWeek == 4) { // Wednesday
            return 2;
        } else if (dayInWeek == 5) { // Thursday
            return 3;
        } else if (dayInWeek == 6) { // Friday
            return 4;
        } else if (dayInWeek == 7) { // Saturday
            return 5;
        }
        return -1;
    }

    public static String fromRepeatOnStringToDayInWeek(String repeatOnString) {
        String daysInWeek = "";
        if (repeatOnString.matches("[0]{7}")){
            daysInWeek = "No repeat day";
            return daysInWeek;
        }
        if (String.format("%c", repeatOnString.charAt(0)).equals("1")) {
            daysInWeek += "mon,";
        }
        if (String.format("%c", repeatOnString.charAt(1)).equals("1")) {
            daysInWeek += "tue,";
        }
        if (String.format("%c", repeatOnString.charAt(2)).equals("1")) {
            daysInWeek += "wed,";
        }
        if (String.format("%c", repeatOnString.charAt(3)).equals("1")) {
            daysInWeek += "thu,";
        }
        if (String.format("%c", repeatOnString.charAt(4)).equals("1")) {
            daysInWeek += "fri,";
        }
        if (String.format("%c", repeatOnString.charAt(5)).equals("1")) {
            daysInWeek += "sat,";
        }
        if (String.format("%c", repeatOnString.charAt(6)).equals("1")) {
            daysInWeek += "sun";
        }
        if (String.format("%c", daysInWeek.trim().charAt(daysInWeek.length() - 1)).equals(",")) {
            return daysInWeek.substring(0, daysInWeek.length() - 1);
        }
        return daysInWeek;
    }

    public static Long startDayInMonth(Long dateTimestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(dateTimestamp));

        // get the first day of month;
        int startDateOfMonth = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, startDateOfMonth);
        calendar.set(Calendar.AM_PM, Calendar.AM);

        // set hour, minutes, seconds, milliseconds at start date
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    public static Long endDayInMonth(Long dateTimestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(dateTimestamp));

        // get the last day of month;
        int endDateOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, endDateOfMonth);
        calendar.set(Calendar.AM_PM, Calendar.PM);

        // set hour, minutes, seconds, milliseconds at end date
        calendar.set(Calendar.HOUR, 11);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return calendar.getTimeInMillis();
    }

    public static Long getCurrentDayTimestamp() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    public static String getCurrentWeekday() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        HashMap<Integer, String> map = new HashMap<>();
        map.put(Calendar.SUNDAY, "sun"); map.put(Calendar.MONDAY, "mon");
        map.put(Calendar.TUESDAY, "tue"); map.put(Calendar.WEDNESDAY, "wed");
        map.put(Calendar.THURSDAY, "thu"); map.put(Calendar.FRIDAY, "fri"); map.put(Calendar.SATURDAY, "sat");
        return  map.get(day);
    }
}
