package io.github.idoqo.radario.helpers;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class DateTimeHelper {
    public static final String YEARS = "year";
    public static final String MONTHS = "months";
    public static final String WEEKS = "weeks";
    public static final String DAYS = "days";
    public static final String HOURS = "hours";
    public static final String MINUTES = "minutes";
    public static final String SECONDS = "seconds";
    /**
     * Get a diff between two dates
     * @param date1 the older date
     * @param date2 the newer date
     * @param timeUnit the unit in which you want the diff
     * @return the diff value, in the provided unit
     */
    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }


    public static HashMap<String, Long> deduceDuration (long millseconds) {
        HashMap<String, Long> retVal = new HashMap<>();

        long days = TimeUnit.MILLISECONDS.toDays(millseconds);
        long hours = TimeUnit.MILLISECONDS.toHours(millseconds);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millseconds);

        retVal.put(DAYS, days);
        retVal.put(HOURS, hours);

        return retVal;
    }
}
