package be.cegeka.batchers.taxservice.stubwebservice;

import be.cegeka.batchers.taxcalculator.to.TaxServiceResponse;
import be.cegeka.batchers.taxcalculator.to.TaxTo;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class TaxController {

    public static final String RESPONSE_BODY_SUCCESS = "OK";
    public static final String RESPONSE_BODY_FAIL = "FAILURE";
    @Autowired
    TextFileTaxLogger taxLogger;
    @Autowired
    SpecialEmployeesService specialEmployeesService;

    @RequestMapping(value = "/taxservice", method = POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<TaxServiceResponse> submitTaxForm(@RequestBody @Valid TaxTo taxTo) throws JsonProcessingException {
        if (specialEmployeesService.isEmployeeBlacklisted(taxTo.getEmployeeId())) {
            taxLogger.log(taxTo, RESPONSE_BODY_FAIL);
            return new ResponseEntity<>(new TaxServiceResponse(RESPONSE_BODY_FAIL), HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            specialEmployeesService.sleepIfNecessary(taxTo.getEmployeeId());
            taxLogger.log(taxTo, RESPONSE_BODY_SUCCESS);
            return new ResponseEntity<>(new TaxServiceResponse(RESPONSE_BODY_SUCCESS), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/reset", method = POST)
    public ResponseEntity<?> resetSpecialEmployeesService() {
        specialEmployeesService.reset();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
