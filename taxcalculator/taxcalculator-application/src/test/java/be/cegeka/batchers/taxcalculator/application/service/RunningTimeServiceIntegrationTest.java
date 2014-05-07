package be.cegeka.batchers.taxcalculator.application.service;

import be.cegeka.batchers.taxcalculator.application.infrastructure.IntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.fest.assertions.api.Assertions.assertThat;

public class RunningTimeServiceIntegrationTest extends IntegrationTest {
    @Autowired
    RunningTimeService runningTimeService;

    @Test
    public void testValuesAreFromConfigFile() throws Exception {
        assertThat(runningTimeService.getMinimumTime()).isEqualTo(1L);
        assertThat(runningTimeService.getMaximumTime()).isEqualTo(2L);
    }
}
