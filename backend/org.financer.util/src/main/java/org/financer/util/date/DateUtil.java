package org.financer.util.date;

import java.time.LocalDate;
import java.time.Period;

public class DateUtil {

    private DateUtil() {
        super();
    }

    public static int getMonthDifference(LocalDate date1, LocalDate date2) {
        return (12 * Period.between(date1.withDayOfMonth(1), date2.withDayOfMonth(1)).getYears()) +
                Period.between(date1.withDayOfMonth(1), date2.withDayOfMonth(1)).getMonths();
    }

    public static boolean checkIfMonthsAreEqual(LocalDate date1, LocalDate date2) {
        return (date1.getYear() == date2.getYear() && date1.getMonthValue() == date2.getMonthValue());
    }

    public static boolean isDateBetween(LocalDate date, LocalDate startDate, LocalDate endDate) {
        return getMonthDifference(date, startDate) <= 0 && getMonthDifference(date, endDate) >= 0;
    }
}
