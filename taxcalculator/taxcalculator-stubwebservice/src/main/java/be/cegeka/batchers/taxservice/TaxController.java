package be.cegeka.batchers.taxservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class TaxController {

    public static final String RESPONSE_BODY_FAIL = "FAILURE";
    @Autowired
    TaxSubmissionLogger taxSubmissionLogger;

    @Autowired
    BlacklistEmployeesService blacklistEmployeesService;

    @RequestMapping (value="/taxservice", method = POST)
    @ResponseBody
    public ResponseEntity<String> submitTaxForm(@RequestBody @Valid TaxTo taxTo){
        ResponseEntity<String> response = null;

        if(blacklistEmployeesService.isEmployeeBlacklisted(taxTo.getEmployeeId())){
            response = new ResponseEntity<String>(RESPONSE_BODY_FAIL, HttpStatus.BAD_REQUEST);
        }else{
            String okStatus = "OK";
            taxSubmissionLogger.log(taxTo,okStatus);
            response = new ResponseEntity<String>(okStatus, HttpStatus.OK);
        }

        return response;

    }

}
