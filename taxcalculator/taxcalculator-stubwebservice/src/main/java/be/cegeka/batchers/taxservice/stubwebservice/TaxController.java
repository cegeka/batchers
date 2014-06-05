package be.cegeka.batchers.taxservice.stubwebservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class TaxController {

    public static final String RESPONSE_BODY_SUCCESS = "OK";
    public static final String RESPONSE_BODY_FAIL = "FAILURE";
    public static final String EMPLOYEE_ID = "employeeId";
    public static final String AMOUNT = "amount";
    @Autowired
    TextFileTaxLogger taxLogger;
    @Autowired
    SpecialEmployeesService specialEmployeesService;

    @RequestMapping(value = "/taxservice", method = POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<JsonNode> submitTaxForm(@RequestBody JsonNode jsonNode) throws JsonProcessingException {
        if(isNotValid(jsonNode)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (specialEmployeesService.isEmployeeBlacklisted(getEmployeeId(jsonNode))) {
            taxLogger.log(getEmployeeId(jsonNode), getTaxAmount(jsonNode), RESPONSE_BODY_FAIL);
            return new ResponseEntity<>(createResponse(RESPONSE_BODY_FAIL), HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            specialEmployeesService.sleepIfNecessary(getEmployeeId(jsonNode));
            taxLogger.log(getEmployeeId(jsonNode), getTaxAmount(jsonNode), RESPONSE_BODY_SUCCESS);
            return new ResponseEntity<>(createResponse(RESPONSE_BODY_SUCCESS), HttpStatus.OK);
        }
    }

    private boolean isNotValid(JsonNode jsonNode) {
        return !(jsonNode.hasNonNull(EMPLOYEE_ID) && jsonNode.hasNonNull(AMOUNT));
    }

    @RequestMapping(value = "/reset", method = POST)
    public ResponseEntity resetSpecialEmployeesService() {
        specialEmployeesService.reset();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Long getEmployeeId(JsonNode jsonNode) {
        return jsonNode.get(EMPLOYEE_ID).asLong();
    }

    private Double getTaxAmount(JsonNode jsonNode) {
        return jsonNode.get(AMOUNT).asDouble();
    }

    private JsonNode createResponse(String status) {
        ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
        objectNode.put("status", status);
        return objectNode;
    }
}
