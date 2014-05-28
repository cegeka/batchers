package be.cegeka.batchers.taxcalculator.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RunningTimeService {

    private static final Logger LOG = LoggerFactory.getLogger(RunningTimeService.class);

    @Value(value = "${runningtimeservice.minimumTime:0}")
    private int minimumTime = 0;
    @Value(value = "${runningtimeservice.maximumTime:0}")
    private int maximumTime = 0;

    public void sleep() {
        if (minimumTime > 0 && maximumTime >= minimumTime) {
            try {
                actuallySleep(calculateSleepTime());
            } catch (InterruptedException e) {
                LOG.error("Sleep error", e);
            }
        }
    }

    protected void actuallySleep(long sleepTime) throws InterruptedException {
        Thread.sleep(sleepTime);
    }

    protected long calculateSleepTime() {
        return minimumTime + new Random().nextInt(maximumTime - minimumTime);
    }

    public long getMinimumTime() {
        return minimumTime;
    }

    public void setMinimumTime(int minimumTime) {
        this.minimumTime = minimumTime;
    }

    public long getMaximumTime() {
        return maximumTime;
    }

    public void setMaximumTime(int maximumTime) {
        this.maximumTime = maximumTime;
    }
}
