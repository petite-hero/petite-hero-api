package capstone.petitehero;

import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.Verification;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public class MAINTEST {

    public static final String ACCOUNT_SID = "AC87ee71564e40de1799313b7213ad98e7";
    public static final String AUTH_TOKEN = "dd02022b23f34c10a4d51115ebaaca3a";

    public static void main(String[] args) {
        Date d = new Date();
        Instant instant = Instant.ofEpochMilli(d.getTime());

        LocalDateTime localDateTime = LocalDateTime.now();
        Calendar c = Calendar.getInstance();
        c.setTime(d);

        c.set(Calendar.AM_PM, Calendar.AM);
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        System.out.println("Date E: " + c.getTimeInMillis());

        c.set(Calendar.AM_PM, Calendar.PM);
        c.set(Calendar.HOUR, 11);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        System.out.println("Date M: " + c.getTimeInMillis());
//        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
//        Verification verification = Verification.creator(
//            "VA4dd185162c014db865f3ba28cb1b8538",
//                "+84 93 819 4701",
//                "sms"
//        ).create();
    }
}
