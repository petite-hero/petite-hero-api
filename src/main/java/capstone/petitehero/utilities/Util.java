package capstone.petitehero.utilities;

import capstone.petitehero.config.common.Constants;
import capstone.petitehero.dtos.ResponseObject;
import capstone.petitehero.dtos.common.CRONJobChildDTO;
import capstone.petitehero.dtos.response.location.GetListSafeZoneByDateResponseDTO;
import capstone.petitehero.dtos.response.location.SafeZoneChangedResponseDTO;
import capstone.petitehero.dtos.response.quest.ListQuestResponseDTO;
import capstone.petitehero.dtos.response.task.ListTaskResponseDTO;
import capstone.petitehero.entities.*;
import capstone.petitehero.dtos.common.SummaryTaskDetail;
import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Util {

    public static String encodePassword(String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        String generatedPassword;
        KeySpec spec = new PBEKeySpec(password.toCharArray(), Constants.SALT, Constants.LOOP, Constants.LENGTH_PASSWORD);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

        byte[] bytes = factory.generateSecret(spec).getEncoded();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        generatedPassword = sb.toString();
        return generatedPassword;
    }

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

//    public static boolean validateLengthOfString(String str, int min, int max) {
//        return str.length() >= min && str.length() <= max;
//    }

//    public static boolean validateName(String str, int min, int max) {
//        // p{L} regex for unicode characters
//        return str.matches("[\\p{L}\\s0-9]{" + min + "," + max + "}");
//    }
//
//    public static boolean validateTimestamp(String timestamp) {
//        return timestamp.matches("\\-?(\\d+)");
//    }

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

    public static String formatTimestampToDate(Long timeStamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return sdf.format(new Date(timeStamp));
    }

    // for filter list to distinct list
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public static Long getStartDay(Long timeStampDate) {
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

    public static Long getEndDay(Long timeStampDate) {
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
        if (repeatOnString.matches("[0]{7}")) {
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

    public static String getCurrentWeekday() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        HashMap<Integer, String> map = new HashMap<>();
        map.put(Calendar.SUNDAY, "sun");
        map.put(Calendar.MONDAY, "mon");
        map.put(Calendar.TUESDAY, "tue");
        map.put(Calendar.WEDNESDAY, "wed");
        map.put(Calendar.THURSDAY, "thu");
        map.put(Calendar.FRIDAY, "fri");
        map.put(Calendar.SATURDAY, "sat");
        return map.get(day);
    }

    public static String getCurrentWeekdayByInput(Long inputDate) {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date(inputDate);
        calendar.setTime(date);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        HashMap<Integer, String> map = new HashMap<>();
        map.put(Calendar.SUNDAY, "sun");
        map.put(Calendar.MONDAY, "mon");
        map.put(Calendar.TUESDAY, "tue");
        map.put(Calendar.WEDNESDAY, "wed");
        map.put(Calendar.THURSDAY, "thu");
        map.put(Calendar.FRIDAY, "fri");
        map.put(Calendar.SATURDAY, "sat");
        return map.get(day);
    }

    public static String getCurrentWeekdayRegex() {
        String weekday = getCurrentWeekday();
        HashMap<String, String> map = new HashMap<>();
        map.put("sun", Constants.SUN_REGEX);
        map.put("mon", Constants.MON_REGEX);
        map.put("tue", Constants.TUE_REGEX);
        map.put("wed", Constants.WED_REGEX);
        map.put("thu", Constants.THU_REGEX);
        map.put("fri", Constants.FRI_REGEX);
        map.put("sat", Constants.SAT_REGEX);
        return map.get(weekday);
    }

    public static String getWeekdayRegex(Long date) {
        String weekday = getCurrentWeekdayByInput(date);
        HashMap<String, String> map = new HashMap<>();
        map.put("sun", Constants.SUN_REGEX);
        map.put("mon", Constants.MON_REGEX);
        map.put("tue", Constants.TUE_REGEX);
        map.put("wed", Constants.WED_REGEX);
        map.put("thu", Constants.THU_REGEX);
        map.put("fri", Constants.FRI_REGEX);
        map.put("sat", Constants.SAT_REGEX);
        return map.get(weekday);
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

    public static List<CRONJobChildDTO> castToCronObject(List<Object[]> input) {
        List<CRONJobChildDTO> result = new ArrayList<>();

        Iterator it = input.iterator();
        while (it.hasNext()) {
            Object[] line = (Object[]) it.next();
            CRONJobChildDTO cronJobChildDTO = new CRONJobChildDTO();
            cronJobChildDTO.setChildId(Long.parseLong(line[0].toString()));
            cronJobChildDTO.setPushToken(line[1].toString());
            result.add(cronJobChildDTO);
        }
        return result;
    }

    public static List<GetListSafeZoneByDateResponseDTO> castToSafeZoneResponse(List<Safezone> input) {
        List<GetListSafeZoneByDateResponseDTO> result = new ArrayList<>();
        try {
            GetListSafeZoneByDateResponseDTO temp;
            for (Safezone safezone : input) {
                temp = new GetListSafeZoneByDateResponseDTO(safezone.getSafezoneId(), safezone.getName(), safezone.getLatitude(), safezone.getLongitude(), safezone.getDate(), safezone.getRadius(), safezone.getRepeatOn(), safezone.getFromTime(), safezone.getToTime(), safezone.getType(), safezone.getChild().getChildId(), safezone.getParent().getId());
                result.add(temp);
            }
        } catch (Exception e) {
            System.out.println("Error at castToSafeZoneResponse: " + e.toString());
            e.printStackTrace();
        }
        return result;
    }

    public static Long getCurrentDateMilliValue() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 00);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        calendar.set(Calendar.MILLISECOND, 000);
        return calendar.getTimeInMillis();
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
        StringBuffer url = new StringBuffer();
        url.append(scheme).append("://").append(serverName);
        if ((serverPort != 80) && (serverPort != 443)) {
            url.append(":").append(serverPort);
        }
        url.append(contextPath);
        if (url.toString().endsWith("/")) {
            url.append("/");
        }
        return url.toString();
    }

    public static List<ListTaskResponseDTO> notiTasksAtCurrentDateForChild(List<Task> taskList) {
        List<ListTaskResponseDTO> result = new ArrayList<>();
        if (taskList != null) {
            if (!taskList.isEmpty()) {
                for (Task task : taskList) {
                    ListTaskResponseDTO resultData = new ListTaskResponseDTO();

                    resultData.setTaskId(task.getTaskId());
                    resultData.setName(task.getName());
                    resultData.setDescription(task.getDescription());
                    resultData.setFromTime(Util.formatTimestampToTime(task.getFromTime().getTime()));
                    resultData.setToTime(Util.formatTimestampToTime(task.getToTime().getTime()));
                    resultData.setType(task.getType());

                    result.add(resultData);
                }
            }
        }
        return result;
    }

    public static List<ListQuestResponseDTO> getChildListOfQuest(List<Quest> questList) {
        if (questList != null) {
            List<ListQuestResponseDTO> result = new ArrayList<>();
            if (!questList.isEmpty()) {
                questList.sort(Comparator.comparing(Quest::getCreatedDate).reversed());
                for (Quest questResult : questList) {
                    ListQuestResponseDTO resultData = new ListQuestResponseDTO();

                    resultData.setQuestId(questResult.getQuestId());
                    resultData.setName(questResult.getName());
                    resultData.setTitle(questResult.getTitle());
                    resultData.setStatus(questResult.getStatus());
                    resultData.setDescription(questResult.getDescription());

                    if (questResult.getReward() != null) {
                        resultData.setReward(questResult.getReward());
                    }

                    result.add(resultData);
                }
            }
            return result;
        }
        return null;
    }

    public static String formatTimeCronjob(String timeCronjob) {
        String result = "";
        String[] tempStr = timeCronjob.split(":");
        for (int i = 0; i < tempStr.length; i++) {
            result += tempStr[i];
        }
        return result + " * * ?";
    }

    public static SafeZoneChangedResponseDTO convertSafeZoneToReponseObj(Safezone input, String status) {
        SafeZoneChangedResponseDTO result = new SafeZoneChangedResponseDTO();
        if (status.equals(Constants.DELETED)) {
            result.setStatus(Constants.DELETED);
            result.setSafezoneId(input.getSafezoneId());
        } else {
            result.setStatus(status);
            if (input.getSafezoneId() != null) {
                result.setSafezoneId(input.getSafezoneId());
            }
            if (input.getName() != null && !input.getName().isEmpty()) {
                result.setName(input.getName());
            }
            if (input.getLatitude() != null) {
                result.setLatitude(input.getLatitude());
            }
            if (input.getLongitude() != null) {
                result.setLongitude(input.getLongitude());
            }
            if (input.getFromTime() != null) {
                result.setFromTime(input.getFromTime());
            }
            if (input.getToTime() != null) {
                result.setToTime(input.getToTime());
            }
            if (input.getDate() != null) {
                result.setDate(input.getDate());
            }
            if (input.getRepeatOn() != null && !input.getRepeatOn().isEmpty()) {
                result.setRepeatOn(input.getRepeatOn());
            }
            if (input.getRadius() != null) {
                result.setRadius(input.getRadius());
            }
            if (input.getIsDisabled() != null) {
                result.setIsDisabled(input.getIsDisabled());
            }
            if (input.getType() != null && !input.getType().isEmpty()) {
                result.setType(input.getType());
            }
            if (input.getChild().getChildId() != null) {
                result.setChild(input.getChild().getChildId());
            }
            if (input.getParent().getId() != null) {
                result.setParent(input.getParent().getId());
            }
        }
        return result;
    }
}
