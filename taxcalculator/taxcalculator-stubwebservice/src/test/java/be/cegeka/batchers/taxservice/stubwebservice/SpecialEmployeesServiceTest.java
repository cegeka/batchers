package be.cegeka.batchers.taxservice.stubwebservice;

import org.junit.Test;

import java.util.Date;

import static org.fest.assertions.api.Assertions.assertThat;

public class SpecialEmployeesServiceTest {

    public static final int TIMEOUT = 20;
    private Long specialEmployeeID1 = 1L;
    private Long specialEmployeeID2 = 2L;
    private Long specialEmployeeID3 = 3L;

    private SpecialEmployeesService specialEmployeesService = new SpecialEmployeesService();

    @Test
    public void givenBlacklistEmployees_whenEmployeeBlacklistedOneTime_thenReturnTrueFirstTime_FalseSecondTime() {
        specialEmployeesService.setBlacklistedEmployees("1:1,2:1,3:1");

        assertThat(specialEmployeesService.isEmployeeBlacklisted(specialEmployeeID1)).isTrue();
        assertThat(specialEmployeesService.isEmployeeBlacklisted(specialEmployeeID1)).isFalse();
        assertThat(specialEmployeesService.isEmployeeBlacklisted(specialEmployeeID2)).isTrue();
        assertThat(specialEmployeesService.isEmployeeBlacklisted(specialEmployeeID2)).isFalse();
        assertThat(specialEmployeesService.isEmployeeBlacklisted(specialEmployeeID3)).isTrue();
        assertThat(specialEmployeesService.isEmployeeBlacklisted(specialEmployeeID3)).isFalse();
    }

    @Test
    public void givenBlacklistEmployees_whenEmployeeBlacklistedSeveralTimes_thenReturnCorrectBlacklist() {
        specialEmployeesService.setBlacklistedEmployees("1:2");

        assertThat(specialEmployeesService.isEmployeeBlacklisted(specialEmployeeID1)).isTrue();
        assertThat(specialEmployeesService.isEmployeeBlacklisted(specialEmployeeID1)).isTrue();
        assertThat(specialEmployeesService.isEmployeeBlacklisted(specialEmployeeID1)).isFalse();
    }

    @Test
    public void givenTimeoutEmployees_whenIsEmployeeTimeout_thenReturnTrue() {
        specialEmployeesService.setTimeoutEmployees("1:1,2:1,3:1");

        assertThat(specialEmployeesService.isEmployeeTimeout(specialEmployeeID1)).isTrue();
        assertThat(specialEmployeesService.isEmployeeTimeout(specialEmployeeID2)).isTrue();
        assertThat(specialEmployeesService.isEmployeeTimeout(specialEmployeeID3)).isTrue();
    }

    @Test
    public void givenTimeoutEmployee_whenSleepIfNecessary_thenSleepForTimeoutMilliseconds() throws InterruptedException {
        specialEmployeesService.setTimeoutEmployees("1:1");
        specialEmployeesService.setTimeout(TIMEOUT);
        long initialMillis = new Date().getTime();

        specialEmployeesService.sleepIfNecessary(specialEmployeeID1);

        assertThat(new Date().getTime() - initialMillis).isGreaterThanOrEqualTo(TIMEOUT);
    }

    @Test
    public void givenNonTimeoutEmployee_whenSleepIfNecessary_thenDontSleep() throws InterruptedException {
        specialEmployeesService.setTimeout(TIMEOUT);
        long initialMillis = new Date().getTime();

        specialEmployeesService.sleepIfNecessary(1L);

        assertThat(new Date().getTime() - initialMillis).isLessThan(TIMEOUT);
    }

}
