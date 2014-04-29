package be.cegeka.batchers.springbatch.be.cegeka.batchers.taxreporting;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created with IntelliJ IDEA.
 * User: raduci
 * Date: 28.04.2014
 * Time: 17:00
 * To change this template use File | Settings | File Templates.
 */

@Controller
public class TaxReportingService {

    @RequestMapping (value="/taxreport", method = GET)
    public String submitTaxForm(){

        return "OK";

    }
}
