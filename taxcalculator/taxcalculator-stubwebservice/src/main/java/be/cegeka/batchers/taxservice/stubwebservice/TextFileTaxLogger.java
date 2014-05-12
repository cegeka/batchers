package be.cegeka.batchers.taxservice.stubwebservice;

import be.cegeka.batchers.taxcalculator.to.TaxTo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TextFileTaxLogger {

    static Logger LOGGER = LoggerFactory.getLogger(TextFileTaxLogger.class);

    public void log(TaxTo taxTo, String status) {
        LOGGER.info("Received tax request:" + taxTo + " completed with status: " + status);
    }
}
