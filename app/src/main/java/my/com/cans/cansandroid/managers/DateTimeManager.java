package my.com.cans.cansandroid.managers;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Rfeng on 15/11/2016.
 */

public class DateTimeManager {
    //1 minute = 60 seconds
    //1 hour = 60 x 60 = 3600
    //1 day = 3600 x 24 = 86400
    public static String countDown(Date startDate, Date endDate) {

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        if (elapsedDays == 1)
            return "a day";
        else if (elapsedDays == -1)
            return "a day ago";
        else if (elapsedDays > 0)
            return String.format("%d days", elapsedDays);
        else if (elapsedDays < 0)
            return new SimpleDateFormat("dd MMM yyyy").format(endDate);

        else if (elapsedHours == 1)
            return "an hour";
        else if (elapsedHours == -1)
            return "an hour ago";
        else if (elapsedHours > 0)
            return String.format("%d hours", elapsedHours);
        else if (elapsedHours < 0)
            return String.format("%d hours ago", -elapsedHours);

        else if (elapsedMinutes == 1)
            return "a minute";
        else if (elapsedMinutes == -1)
            return "a minute ago";
        else if (elapsedMinutes > 0)
            return String.format("%d minutes", elapsedMinutes);
        else if (elapsedMinutes < 0)
            return String.format("%d minutes ago", -elapsedMinutes);

        else if (elapsedSeconds > 0)
            return "few seconds";
        else
            return "few seconds ago";
    }
}
