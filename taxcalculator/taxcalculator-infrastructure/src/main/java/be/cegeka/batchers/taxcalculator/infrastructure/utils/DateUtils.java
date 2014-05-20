package be.cegeka.batchers.taxcalculator.infrastructure.utils;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.Locale;

public class DateUtils {
    private DateUtils() {
    }

    public static String longMonthOf(DateTime dateTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, dateTime.getMonthOfYear() - 1);

        return calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
    }

    public static String longMonthAndYearOf(DateTime dateTime) {
        return longMonthOf(dateTime) + " " + dateTime.getYear();
    }
}
