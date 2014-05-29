package be.cegeka.batchers.taxcalculator.presentation.rest.controller;

import be.cegeka.batchers.taxcalculator.application.domain.EmployeeGenerator;
import be.cegeka.batchers.taxcalculator.application.domain.EmployeeService;
import be.cegeka.batchers.taxcalculator.presentation.rest.model.GenerateEmployeesModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/generateEmployees")
public class GenerateEmployeesRestController {
    private static final Logger LOG = LoggerFactory.getLogger(GenerateEmployeesRestController.class);

    @Autowired
    protected EmployeeGenerator employeeGenerator;

    @Autowired
    protected EmployeeService employeeService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> generateEmployees(GenerateEmployeesModel generateEmployeesModel) {
        Long employeesCount = generateEmployeesModel.getEmployeesCount();
        LOG.debug("Generating " + employeesCount + " employees in rest controller");

        Long count = employeeService.count();
        if (count > employeesCount) {
            employeeService.truncate();
        } else {
            employeesCount = employeesCount - count;
        }
        employeeGenerator.setNumberOfEmployees(employeesCount);
        employeeGenerator.generateAll();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
