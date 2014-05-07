package be.cegeka.batchers.taxcalculator.service;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class RunningTimeServiceTest {

    public static final int MINIMUM_TIME = 100;
    public static final int MAXIMUM_TIME = 500;
    RunningTimeService runningTimeService = new RunningTimeService();
    private long counter;

    @Test
    public void testNoSleepTimeWhenMinimumAndMaximumNotSet() throws Exception {
        startCounter();
        runningTimeService.sleep();
        stopCounter();

        assertThat(counter).isZero();
    }

    @Test
    public void testSleepForTimeBetweenMinimumAndMaximum() throws Exception {
        runningTimeService.setMinimumTime(MINIMUM_TIME);
        runningTimeService.setMaximumTime(MAXIMUM_TIME);

        startCounter();
        runningTimeService.sleep();
        stopCounter();

        assertThat(counter).isGreaterThanOrEqualTo(MINIMUM_TIME).isLessThanOrEqualTo(500L);
    }

    @Test
    public void testNoSleepWhenMaximumMissing() throws Exception {
        runningTimeService.setMinimumTime(MINIMUM_TIME);
        startCounter();
        runningTimeService.sleep();
        stopCounter();

        assertThat(counter).isZero();
    }

    @Test
    public void testNoSleepWhenMinimumMissing() throws Exception {
        runningTimeService.setMaximumTime(MAXIMUM_TIME);
        startCounter();
        runningTimeService.sleep();
        stopCounter();

        assertThat(counter).isZero();
    }

    private void stopCounter() {
        counter = System.currentTimeMillis() - counter;
    }

    private void startCounter() {
        counter = System.currentTimeMillis();
    }
}
