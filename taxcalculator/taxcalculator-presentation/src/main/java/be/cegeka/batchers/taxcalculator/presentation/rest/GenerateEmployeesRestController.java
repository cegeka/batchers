package be.cegeka.batchers.taxcalculator.presentation.rest;

import be.cegeka.batchers.taxcalculator.application.domain.EmployeeGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/generateEmployees")
public class GenerateEmployeesRestController {
    private static final Logger LOG = LoggerFactory.getLogger(GenerateEmployeesRestController.class);

    @Autowired
    protected EmployeeGenerator employeeGenerator;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public void generateEmployees() {
        long numberOfEmployees = 500L;

        LOG.debug("Generating " + numberOfEmployees + " employees in rest controller");

        employeeGenerator.setNumberOfEmployees(numberOfEmployees);
        employeeGenerator.generateAll();
    }

}
