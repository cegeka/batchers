package be.cegeka.batchers.taxservice.stubwebservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TextFileTaxLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(TextFileTaxLogger.class);

    public void log(Long employeeId, double taxAmount, String status) {
        LOGGER.info("Received tax request for employee with id " + employeeId + " with amount " + taxAmount + " and completed with status: " + status);
    }
}
