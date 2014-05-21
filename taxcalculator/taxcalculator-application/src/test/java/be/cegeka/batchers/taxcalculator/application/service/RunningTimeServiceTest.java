package be.cegeka.batchers.taxcalculator.application.service;

import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

public class RunningTimeServiceTest {

    public static final int MINIMUM_TIME = 100;
    public static final int MAXIMUM_TIME = 500;
    RunningTimeService runningTimeService = new RunningTimeService();

    @Before
    public void setup() {
        runningTimeService = spy(runningTimeService);
    }

    @Test
    public void testNoSleepTimeWhenMinimumAndMaximumNotSet() throws Exception {
        runningTimeService.sleep();

        verify(runningTimeService, times(0)).actuallySleep(anyLong());
    }

    @Test
    public void testSleepWhenMinimumAndMaximumAreSet() throws Exception {
        runningTimeService.setMinimumTime(MINIMUM_TIME);
        runningTimeService.setMaximumTime(MAXIMUM_TIME);

        doReturn(300L).when(runningTimeService).calculateSleepTime();
        runningTimeService.sleep();

        verify(runningTimeService, times(1)).actuallySleep(300L);
    }

    @Test
    public void testNoSleepWhenMaximumMissing() throws Exception {
        runningTimeService.setMinimumTime(MINIMUM_TIME);
        runningTimeService.sleep();
        verify(runningTimeService, times(0)).actuallySleep(anyLong());

    }

    @Test
    public void testNoSleepWhenMinimumMissing() throws Exception {
        runningTimeService.setMaximumTime(MAXIMUM_TIME);
        runningTimeService.sleep();

        verify(runningTimeService, times(0)).actuallySleep(anyLong());
    }

    @Test
    public void givenMaximumAndMinimum_whenCalculateSleepTime_thenSleepTimeIsRandomlyBetweenMinimumAndMaximum() {
        runningTimeService.setMinimumTime(MINIMUM_TIME);
        runningTimeService.setMaximumTime(MAXIMUM_TIME);

        long sleepTime = runningTimeService.calculateSleepTime();

        assertThat(sleepTime).isGreaterThanOrEqualTo(MINIMUM_TIME).isLessThanOrEqualTo(MAXIMUM_TIME);
    }
}
