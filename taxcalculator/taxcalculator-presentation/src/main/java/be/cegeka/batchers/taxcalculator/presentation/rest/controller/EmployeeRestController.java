package be.cegeka.batchers.taxcalculator.presentation.rest.controller;

import be.cegeka.batchers.taxcalculator.application.domain.EmployeeService;
import be.cegeka.batchers.taxcalculator.to.EmployeeTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value = "/employees")
public class EmployeeRestController {
    @Autowired
    EmployeeService employeeService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<EmployeeTo> getEmployees(@RequestParam int page, @RequestParam int pageSize) {
        return employeeService.getEmployees(page, pageSize);
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    @ResponseBody
    public long getEmployeeCount() {
        return employeeService.getEmployeeCount();
    }
}
