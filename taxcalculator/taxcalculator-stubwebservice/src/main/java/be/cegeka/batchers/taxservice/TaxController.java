package be.cegeka.batchers.taxservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class TaxController {

    @Autowired
    TaxSubmissionLogger taxSubmissionLogger;

    @RequestMapping (value="/taxservice", method = POST)
    @ResponseBody
    public String submitTaxForm(@RequestBody @Valid TaxTo taxTo){
        String status = "OK";
        taxSubmissionLogger.log(taxTo,status);
        return status;

    }

}
