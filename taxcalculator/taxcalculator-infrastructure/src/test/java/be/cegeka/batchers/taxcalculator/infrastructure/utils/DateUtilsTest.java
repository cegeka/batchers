package be.cegeka.batchers.taxcalculator.infrastructure.utils;

import org.fest.assertions.api.Assertions;
import org.joda.time.DateTime;
import org.junit.Test;

public class DateUtilsTest {

    @Test
    public void testLongMonthName() throws Exception {
        DateTime dateTime = new DateTime().withMonthOfYear(1);

        String longMonthName = DateUtils.longMonthOf(dateTime);

        Assertions.assertThat(longMonthName).isEqualTo("January");
    }

    @Test
    public void testLongMonthAndYear() throws Exception {
        DateTime dateTime = new DateTime()
                .withMonthOfYear(2)
                .withYear(2015);

        String longMonthAndYear = DateUtils.longMonthAndYearOf(dateTime);

        Assertions.assertThat(longMonthAndYear).isEqualTo("February 2015");
    }
}
