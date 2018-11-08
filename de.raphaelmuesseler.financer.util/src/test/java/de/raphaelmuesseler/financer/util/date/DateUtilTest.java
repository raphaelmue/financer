package de.raphaelmuesseler.financer.util.date;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class DateUtilTest {
    @Test
    public void testMonthDifference() {
        LocalDate localDate1 = LocalDate.of(2018, 5, 7);
        LocalDate localDate2 = LocalDate.of(2017, 9, 30);

        Assertions.assertEquals(-8, DateUtil.getMonthDifference(localDate1, localDate2));

        localDate1 = LocalDate.of(2018, 5, 7);
        localDate2 = LocalDate.of(2018, 6, 3);

        Assertions.assertEquals(1, DateUtil.getMonthDifference(localDate1, localDate2));

        localDate1 = LocalDate.of(2018, 6, 7);
        localDate2 = LocalDate.of(2018, 6, 3);

        Assertions.assertEquals(0, DateUtil.getMonthDifference(localDate1, localDate2));
    }

    @Test
    public void testMonthEquality() {
        LocalDate localDate1 = LocalDate.of(2018, 5, 7);
        LocalDate localDate2 = LocalDate.of(2017, 9, 30);

        Assertions.assertFalse(DateUtil.checkIfMonthsAreEqual(localDate1, localDate2));

        localDate1 = LocalDate.of(2018, 5, 7);
        localDate2 = LocalDate.of(2018, 6, 3);

        Assertions.assertFalse(DateUtil.checkIfMonthsAreEqual(localDate1, localDate2));

        localDate1 = LocalDate.of(2018, 6, 7);
        localDate2 = LocalDate.of(2018, 6, 3);

        Assertions.assertTrue(DateUtil.checkIfMonthsAreEqual(localDate1, localDate2));
    }
}
