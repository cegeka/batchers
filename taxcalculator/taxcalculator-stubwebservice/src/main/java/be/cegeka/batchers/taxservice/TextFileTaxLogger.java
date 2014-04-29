package be.cegeka.batchers.taxservice;

import org.slf4j.*;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: raduci
 * Date: 29.04.2014
 * Time: 14:27
 * To change this template use File | Settings | File Templates.
 */

@Component
public class TextFileTaxLogger implements  TaxSubmissionLogger{

    static Logger LOGGER = LoggerFactory.getLogger(TextFileTaxLogger.class);
    @Override
    public void log(TaxTo taxTo, String status) {
       LOGGER.info("Received tax request:"+ taxTo + " completed with status: " + status);
    }
}
