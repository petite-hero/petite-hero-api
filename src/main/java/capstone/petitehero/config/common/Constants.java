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

    // role values
    public static final String PARENT = "parent";
    public static final String ADMIN = "admin";


    // status values
    public static final boolean SAFE = true;
    public static final boolean NOT_SAFE = false;
    public static final String WAITING_FOR_ACCEPT = "0";
    public static final String ACCEPTED = "1";
    public static final String DECLINED = "2";
    public static final String NOT_STARTED_YET = "3";
    public static final String ON_GOING = "4";
    public static final String OVERDUE = "5";
    public static final String COMMITED = "6";
    public static final String FINISHED_CONFIRMED = "7";
    public static final String CANNOT_FINISH_CONFIRMED = "8";
    public static final String UPLOAD_FOLDER = "images/";
    public static final String BADGE_FOLDER = "images/badges/";
    public static final Long FREE_TRAIL_TYPE = 1L;

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

    public static final String GET_DATA_SUCCESSFULLY = "Get data successfully!";

    public static final String EMERGENCY = "emergency";
    public static final String STOP_EMERGENCY = "stop_emergency";

    public static final String SERVER_ERROR = "Server Error: ";

    public static final String EXPO_PUSH_NOTI_URL = "https://exp.host/--/api/v2/push/send";
    public static final String FCM_PUSH_NOTI_URL = "https://fcm.googleapis.com/fcm/send";
    public static final String FCM_SERVER_KEY = "key=AAAAWmX8LRw:APA91bHacypoqkRBPkNfT7IN82EHqpvhE9DG_zI_2GmXWAqdlcPb6iX7gLVja2EgsUb1_hUS2E_oV_ZuMCcy8am0vvxLrLt-vbahzg5owmXsOZKKLJSJDLUsoM0Pf-zz3psSb_Wsuexa";
    public static final String PETITE_HERO = "Petite Hero";
    public static final String SILENT_NOTI = "silent-noti";
    public static final String CHILD_SAFE = "Child is currently safe";
    public static final String CHILD_NOT_SAFE = "Child is currently not safe";
    public static final String DONE_SETTING_UP_DEVICE = "Done setting up child's device";

    public static final Long ONE_HOUR_IN_MILLISECOND = Long.parseLong("3600000");

//    // push notification message
//    public static final int CHILD_STATUS_CHANGED = 1;
//    public static final int CHILD_NOT_SAFE = 2;
//    public static final int CHILD_SAFE = 3;




}
