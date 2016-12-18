package io.github.idoqo.radario.helpers;

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

        long seconds = TimeUnit.MILLISECONDS.toSeconds(millseconds);
        long days = TimeUnit.MILLISECONDS.toDays(millseconds);
        long hours = TimeUnit.MILLISECONDS.toHours(millseconds);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millseconds);
        long weeks = days/7;

        retVal.put(WEEKS, weeks);
        retVal.put(DAYS, days);
        retVal.put(HOURS, hours);
        retVal.put(MINUTES, minutes);
        retVal.put(SECONDS, seconds);

        return retVal;
    }

    public static String[] getCountAndUnit(Date older, Date newer){
        long diff = getDateDiff(older, newer, TimeUnit.MILLISECONDS);
        HashMap<String, Long> duration = deduceDuration(diff);
        //a 2 element array, 0 = the count as string, 1 = the unit
        String[] countAndUnit = new String[2];
        long wks = duration.get(WEEKS);
        long days = duration.get(DAYS);
        long hrs = duration.get(HOURS);
        long mins = duration.get(MINUTES);
        long secs = duration.get(SECONDS);

        //if the higher unit is less than one, use the one below it as your unit
        //e.g if minute is less than 1, use seconds as your unit
        if (mins < 1) {
            countAndUnit[0] = String.valueOf(secs);
            countAndUnit[1] = (secs <= 1) ? "second" : "seconds";
        } else if (hrs < 1) {
            countAndUnit[0] = String.valueOf(mins);
            countAndUnit[1] = (mins <= 1) ? "minute" : "minutes";
        } else if (days < 1) {
            countAndUnit[0] = String.valueOf(hrs);
            countAndUnit[1] = (hrs <= 1) ? "hour" : "hours";
        } else if (wks < 1) {
            countAndUnit[0] = String.valueOf(days);
            countAndUnit[1] = (days <= 1) ? "day" : "days";
        } else {
            countAndUnit[0] = String.valueOf(wks);
            countAndUnit[1] = (wks <= 1) ? "wk" : "wks";
        }

        return countAndUnit;
    }
}
