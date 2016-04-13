package sandjentrance.com.sj.utils;

import org.joda.time.DateTime;

import java.util.concurrent.TimeUnit;

/**
 * Created by toidiu on 4/13/16.
 */
public class DateUtil {

    public static boolean isDayOld(DateTime dateTime){
        DateTime now = DateTime.now();
        DateTime dayBeforeNow = now.minus(TimeUnit.DAYS.toMillis(1));
        return dayBeforeNow.isAfter(dateTime);
    }
}
