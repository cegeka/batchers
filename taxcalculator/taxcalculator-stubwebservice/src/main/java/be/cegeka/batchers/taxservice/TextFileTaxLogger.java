package be.cegeka.batchers.taxservice;

import org.slf4j.*;
import org.springframework.stereotype.Component;

@Component
public class TextFileTaxLogger {

    static Logger LOGGER = LoggerFactory.getLogger(TextFileTaxLogger.class);

    public void log(TaxTo taxTo, String status) {
       LOGGER.info("Received tax request:"+ taxTo + " completed with status: " + status);
    }
}
