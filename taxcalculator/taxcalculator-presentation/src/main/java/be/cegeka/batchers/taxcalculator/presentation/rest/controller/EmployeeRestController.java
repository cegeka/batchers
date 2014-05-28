package be.cegeka.batchers.taxcalculator.presentation.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import be.cegeka.batchers.taxcalculator.application.domain.EmployeeService;
import be.cegeka.batchers.taxcalculator.to.EmployeeTo;

@Controller
@RequestMapping(value = "/employees")
public class EmployeeRestController {
	@Autowired
	EmployeeService employeeService;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<EmployeeTo> getFirst20Employees() {
		return employeeService.getFirst20();
	}
}
