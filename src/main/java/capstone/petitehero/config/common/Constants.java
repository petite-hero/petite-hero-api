package capstone.petitehero.config.common;

public class Constants {
    public static final String ADMIN_ROLE = "";
    public static final String USER_ROLE = "";

    // boolean values
    public static final boolean IS_NOT_DELETED = false;
    public static final boolean IS_DELETED = true;
    public static final boolean IS_NOT_DISABLED = true;
    public static final boolean IS_DISABLED = true;
    public static final boolean IS_NOT_READ = false;
    public static final boolean IS_READ = true;
    public static final String CRON_SCHEDULED = "30 0 0 * * ?";
    public static final String TIME_ZONE = "Asia/Bangkok";
    public static final boolean TRACKING_ACTIVE_BOOLEAN = true;
    public static final boolean TRACKING_INACTIVE_BOOLEAN = false;

    // role values
    public static final String PARENT = "Parent";
    public static final String ADMIN = "Admin";
    public static final String CHILD = "Child";

    // provider
    public static final String SMART_WATCH = "SW";
    public static final String MOBILE = "MOBILE";


    // status values
    public static final boolean SAFE = true;
    public static final boolean NOT_SAFE = false;
    public static final String UPLOAD_FOLDER = "images/";
    public static final String BADGE_FOLDER = "images/badges/";
    public static final Long FREE_TRAIL_TYPE = 1L;
    public enum status {
        DONE, FAILED, HANDED,
        ASSIGNED, DELETED, CREATED,
        UPDATED, ADDED, CONFIRMED,
        SUCCESS, CANCELLED, PENDING
    }
    public enum taskType {
        HOUSEWORK, SKILLS, EDUCATION
    }

    // error messages
    public static final String NO_VALUE_PRESENT = "No value present";

    //response values
    public static final int CODE_200 = 200;
    public static final int CODE_400 = 400;
    public static final int CODE_404 = 404;
    public static final int CODE_500 = 500;
    public static final String NO_ERROR = "No Error";
    public static final String ERROR = "Error";
    public static final boolean SUCCESS = true;
    public static final boolean FAILED = false;

    public static final String MON_REGEX = "1(0|1){6}";
    public static final String TUE_REGEX = "(0|1){1}1(0|1){5}";
    public static final String WED_REGEX = "(0|1){2}1(0|1){4}";
    public static final String THU_REGEX = "(0|1){3}1(0|1){3}";
    public static final String FRI_REGEX = "(0|1){4}1(0|1){2}";
    public static final String SAT_REGEX = "(0|1){5}1(0|1){1}";
    public static final String SUN_REGEX = "(0|1){6}1";

    public static final String GET_DATA_SUCCESSFULLY = "Get data successfully!";

    public static final String EMERGENCY = "emergency";
    public static final String STOP_EMERGENCY = "stop-emergency";
    public static final String TRACKING_ACTIVE = "active";
    public static final String TRACKING_INACTIVE = "inactive";

    public static final String SERVER_ERROR = "Server Error: ";

    public static final String EXPO_PUSH_NOTI_URL = "https://exp.host/--/api/v2/push/send";
    public static final String FCM_PUSH_NOTI_URL = "https://fcm.googleapis.com/fcm/send";
    public static final String FCM_SERVER_KEY = "key=AAAAWmX8LRw:APA91bHacypoqkRBPkNfT7IN82EHqpvhE9DG_zI_2GmXWAqdlcPb6iX7gLVja2EgsUb1_hUS2E_oV_ZuMCcy8am0vvxLrLt-vbahzg5owmXsOZKKLJSJDLUsoM0Pf-zz3psSb_Wsuexa";
    public static final String FAKE_EXPO_TOKEN = "ExponentPushToken[vtoNjlAGxNtmDSzDgbe06o]";
    public static final String PETITE_HERO = "Petite Hero";
    public static final String SILENT_NOTI = "silent-noti";
    public static final String NEW_SAFEZONES = "new-safezones";
    public static final String UPDATED_SAFEZONES = "updated-safezones";
    public static final String NEW_TASKS = "new-tasks";
    public static final String UPDATED_TASKS = "updated-tasks";
    public static final String CHILD_SAFE = "Child is currently safe";
    public static final String CHILD_NOT_SAFE = "Child is currently not safe";
    public static final String DONE_SETTING_UP_DEVICE = "Done setting up child's device";

    public static final Long ONE_HOUR_IN_MILLISECOND = 3600000L;

//    // push notification message
//    public static final int CHILD_STATUS_CHANGED = 1;
//    public static final int CHILD_NOT_SAFE = 2;
//    public static final int CHILD_SAFE = 3;




}
