package be.cegeka.batchers.taxcalculator.batch;

import be.cegeka.batchers.taxcalculator.batch.integration.AbstractIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.fest.assertions.api.Assertions.assertThat;

public class SendPaycheckProcessorITTest extends AbstractIntegrationTest {
    @Autowired
    SendPaycheckProcessor sendPaycheckProcessor;

    @Test
    public void testFromEmailAddress() throws Exception {
        assertThat(sendPaycheckProcessor.payCheckFrom).isEqualTo("finance.paycheck@email.com");
    }
}
