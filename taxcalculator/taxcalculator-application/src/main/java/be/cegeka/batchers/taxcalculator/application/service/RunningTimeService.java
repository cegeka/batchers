package be.cegeka.batchers.taxcalculator.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RunningTimeService {
    @Value(value = "${runningtimeservice.minimumTime:0}")
    private long minimumTime = 0L;
    @Value(value = "${runningtimeservice.maximumTime:0}")
    private long maximumTime = 0L;

    public void sleep() {
        if (minimumTime > 0 && maximumTime >= minimumTime) {
            long sleepTime = minimumTime + new Random().nextInt((int) (maximumTime - minimumTime));
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
            }
        }
    }

    public void setMinimumTime(long minimumTime) {
        this.minimumTime = minimumTime;
    }

    public void setMaximumTime(long maximumTime) {
        this.maximumTime = maximumTime;
    }

    public long getMinimumTime() {
        return minimumTime;
    }

    public long getMaximumTime() {
        return maximumTime;
    }
}
