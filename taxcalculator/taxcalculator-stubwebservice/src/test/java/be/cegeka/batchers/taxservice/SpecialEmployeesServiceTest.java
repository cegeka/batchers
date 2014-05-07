package be.cegeka.batchers.taxservice;

import org.fest.assertions.api.Assertions;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: raduci
 * Date: 07.05.2014
 * Time: 10:34
 * To change this template use File | Settings | File Templates.
 */
public class SpecialEmployeesServiceTest {

    public static final int TIMEOUT = 20;
    private String specialEmployeeID1 = "1";
    private String specialEmployeeID2 = "2";
    private String specialEmployeeID3 = "3";

    private SpecialEmployeesService specialEmployeesService = new SpecialEmployeesService();

    @Test
    public void givenBlacklistEmployees_whenIsEmployeeBlacklisted_thenReturnTrue(){
        specialEmployeesService.setBlacklistedEmployees(new HashSet<String>(Arrays.asList(specialEmployeeID1, specialEmployeeID2, specialEmployeeID3)));

        assertThat(specialEmployeesService.isEmployeeBlacklisted(specialEmployeeID1)).isTrue();
        assertThat(specialEmployeesService.isEmployeeBlacklisted(specialEmployeeID2)).isTrue();
        assertThat(specialEmployeesService.isEmployeeBlacklisted(specialEmployeeID3)).isTrue();
    }

    @Test
    public void givenTimeoutEmployees_whenIsEmployeeTimeout_thenReturnTrue(){
        specialEmployeesService.setTimeoutEmployees(new HashSet<String>(Arrays.asList(specialEmployeeID1, specialEmployeeID2, specialEmployeeID3)));

        assertThat(specialEmployeesService.isEmployeeTimeout(specialEmployeeID1)).isTrue();
        assertThat(specialEmployeesService.isEmployeeTimeout(specialEmployeeID2)).isTrue();
        assertThat(specialEmployeesService.isEmployeeTimeout(specialEmployeeID3)).isTrue();
    }

    @Test
    public void givenTimeoutEmployee_whenSleepIfNecessary_thenSleepForTimeoutMilliseconds() throws InterruptedException {
        specialEmployeesService.setTimeoutEmployees(new HashSet<String>(Arrays.asList(specialEmployeeID1)));
        specialEmployeesService.setTimeout(TIMEOUT);
        long initialMillis = new Date().getTime();

        specialEmployeesService.sleepIfNecessary(specialEmployeeID1);

        assertThat(new Date().getTime() - initialMillis).isGreaterThanOrEqualTo(TIMEOUT);
    }

    @Test
    public void givenNonTimeoutEmployee_whenSleepIfNecessary_thenDontSleep() throws InterruptedException {
        specialEmployeesService.setTimeout(TIMEOUT);
        long initialMillis = new Date().getTime();

        specialEmployeesService.sleepIfNecessary(specialEmployeeID1);

        assertThat(new Date().getTime() - initialMillis).isLessThan(TIMEOUT);
    }

}
