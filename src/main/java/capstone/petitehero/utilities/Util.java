package capstone.petitehero.utilities;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.ResponseObject;
import capstone.petitehero.dtos.common.SummaryTaskDetail;
import capstone.petitehero.entities.Parent;
import capstone.petitehero.entities.Parent_Child;
import capstone.petitehero.entities.Task;
import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Util {

    public static boolean validatePhoneNumberParent(String phoneNumber) {
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

    public static boolean validateEmail(String email) {
        return email.matches("^[a-zA-Z0-9]+@([a-zA-Z]{2,6}\\.)+[a-zA-Z]{2,6}$");
    }

    public static String fromImageFileToBase64String(String photoFileName) {
        try {
            File imageLocation = new File(Constants.UPLOAD_FOLDER + "/" + photoFileName);

            byte[] bytesPhoto = FileUtils.readFileToByteArray(imageLocation);

            return Base64.getEncoder().encodeToString(bytesPhoto);
        } catch (Exception e) {
            return null;
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

    public static boolean validateLengthOfString(String str, int min, int max) {
        return str.length() >= min && str.length() <= max;
    }

    public static boolean validateName(String str, int min, int max) {
        // p{L} regex for unicode characters
        return str.matches("[\\p{L}\\s]{" + min + "," + max + "}");
    }

    public static boolean validateTimestamp(String timestamp) {
        return timestamp.matches("\\-?(\\d+)");
    }

    public static boolean validateTaskType(String taskType) {
        return taskType.equalsIgnoreCase(Constants.taskType.SKILLS.toString())
                || taskType.equalsIgnoreCase(Constants.taskType.HOUSEWORK.toString())
                || taskType.equalsIgnoreCase(Constants.taskType.EDUCATION.toString());
    }

    public static boolean validateQuestStatus(String status) {
        return status.equalsIgnoreCase(Constants.status.ASSIGNED.toString())
                || status.equalsIgnoreCase(Constants.status.DONE.toString())
                || status.equalsIgnoreCase(Constants.status.FAILED.toString());
    }

    public static boolean validateLongNumber(String number) {
        return number.matches("\\d+");
    }

    public static boolean validateFloatNumber(String number) {
        return number.matches("\\d+\\.?\\d*");
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


    public static Long getCurrentDateMilliValue() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 00);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        calendar.set(Calendar.MILLISECOND, 000);
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

    public static String getCurrentWeekdayRegex() {
        String weekday = getCurrentWeekday();
        HashMap<String, String> map = new HashMap<>();
        map.put("sun", Constants.SUN_REGEX); map.put("mon", Constants.MON_REGEX);
        map.put("tue", Constants.TUE_REGEX); map.put("wed", Constants.WED_REGEX);
        map.put("thu", Constants.THU_REGEX); map.put("fri", Constants.FRI_REGEX); map.put("sat", Constants.SAT_REGEX);
        return  map.get(weekday);
    }

    public static Boolean checkSubscriptionWhenParentAddChild(Parent parentAccount) {
        int countMaxChildParentAccount = 0;
        // get data from table parent_child so the data about child of parent will be duplicated
        // filter
        List<Parent_Child> filterChildForParentAccount =
                parentAccount.getParent_childCollection().stream()
                        .filter(Util.distinctByKey(Parent_Child::getChild))
                        .collect(Collectors.toList());

        // only child not disable in the system is count
        for (Parent_Child childOfParent : filterChildForParentAccount) {
            if (!childOfParent.getChild().getIsDisabled().booleanValue()) {
                countMaxChildParentAccount++;
            }
        }
        if (countMaxChildParentAccount >=
                parentAccount.getSubscription().getSubscriptionType().getMaxChildren()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static Boolean checkSubscriptionWhenParentAddCollaborator(Parent parentAccount) {
        int maxCollaborator = 0;
        List<Parent_Child> filterCollaboratorForParent =
                parentAccount.getParent_collaboratorCollection().stream()
                        .filter(Util.distinctByKey(Parent_Child::getCollaborator))
                        .collect(Collectors.toList());

        for (Parent_Child parent_child : filterCollaboratorForParent) {
            if (!parent_child.getCollaborator().getIsDisabled().booleanValue()) {
                maxCollaborator++;
            }
        }

        if (maxCollaborator ==
                parentAccount.getSubscription().getSubscriptionType().getMaxCollaborator().intValue()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static Boolean validateFromTimeToTimeOfTask(Long fromTime, Long toTime) {
        if (fromTime.longValue() > toTime.longValue()) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    // set fromTime, toTime for duplicate task
    // because request body send can more than 1 assigned date
    public static Long setTimeForAssignDate(Long assignedDate, Long time) {
        // get new assigned date for duplicate task
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(assignedDate));

        // get fromTime or toTime
        Calendar timeCalendar = Calendar.getInstance();
        timeCalendar.setTime(new Date(time));

        calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, timeCalendar.get(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, timeCalendar.get(Calendar.MILLISECOND));

        return calendar.getTime().getTime();
    }

    public static SummaryTaskDetail summaryTaskType(List<Task> taskList) {
        SummaryTaskDetail taskDetail = new SummaryTaskDetail();

        taskDetail.setTotalTaskAssigned(taskList.stream().count());
        // count tasks are done
        taskDetail.setTaskDone(taskList
                .stream()
                .filter(task -> task.getStatus().equalsIgnoreCase(Constants.status.DONE.toString()))
                .count());
        // count tasks ar failed
        taskDetail.setTaskFailed(taskList
                .stream()
                .filter(task -> task.getStatus().equalsIgnoreCase(Constants.status.FAILED.toString()))
                .count());
        // count tasks are handed
        taskDetail.setTaskHanded(taskList
                .stream()
                .filter(task -> task.getStatus().equalsIgnoreCase(Constants.status.HANDED.toString()))
                .count());
        //count task are assigned
        taskDetail.setTaskAssigned(taskList
                .stream()
                .filter(task -> task.getStatus().equalsIgnoreCase(Constants.status.ASSIGNED.toString()))
                .count());

        return taskDetail;
    }

    // for paypal to redirect back to our backend after user complete paypal payment
    public static String getBaseURL(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();
        StringBuffer url =  new StringBuffer();
        url.append(scheme).append("://").append(serverName);
        if ((serverPort != 80) && (serverPort != 443)) {
            url.append(":").append(serverPort);
        }
        url.append(contextPath);
        if(url.toString().endsWith("/")){
            url.append("/");
        }
        return url.toString();
    }
}
