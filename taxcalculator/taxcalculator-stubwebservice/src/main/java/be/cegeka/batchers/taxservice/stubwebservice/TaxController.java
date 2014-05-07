package be.cegeka.batchers.taxservice.stubwebservice;

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
    TextFileTaxLogger taxLogger;
    @Autowired
    SpecialEmployeesService specialEmployeesService;

    @RequestMapping(value = "/taxservice", method = POST)
    @ResponseBody
    public ResponseEntity<String> submitTaxForm(@RequestBody @Valid TaxTo taxTo) {
        ResponseEntity<String> response;

        if (specialEmployeesService.isEmployeeBlacklisted(taxTo.getEmployeeId())) {
            response = new ResponseEntity<String>(RESPONSE_BODY_FAIL, HttpStatus.BAD_REQUEST);
        } else {
            String okStatus = "OK";
            specialEmployeesService.sleepIfNecessary(taxTo.getEmployeeId());
            taxLogger.log(taxTo, okStatus);
            response = new ResponseEntity<String>(okStatus, HttpStatus.OK);
        }

        return response;

    }

}
