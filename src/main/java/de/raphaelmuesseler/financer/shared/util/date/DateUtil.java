package de.raphaelmuesseler.financer.shared.util.date;

import java.time.LocalDate;
import java.time.Period;

public class DateUtil {
    public static int getMonthDifference(LocalDate date1, LocalDate date2) {
        return (12 * Period.between(date1.withDayOfMonth(1), date2.withDayOfMonth(1)).getYears()) +
                Period.between(date1.withDayOfMonth(1), date2.withDayOfMonth(1)).getMonths();
    }
}
