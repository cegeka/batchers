package be.cegeka.batchers.taxcalculator.application.service;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.domain.EmployeeRepository;
import be.cegeka.batchers.taxcalculator.application.domain.MonthlyTaxForEmployee;
import be.cegeka.batchers.taxcalculator.application.domain.MonthlyTaxForEmployeeRepository;
import be.cegeka.batchers.taxcalculator.application.domain.generation.EmployeeGeneratorCleaner;
import be.cegeka.batchers.taxcalculator.application.domain.reporting.MonthlyReportRepository;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private MonthlyTaxForEmployeeRepository monthlyTaxForEmployeeRepository;

    public List<Employee> getEmployees(int page, int pageSize) {
        return employeeRepository.getEmployees(page, pageSize);
    }

    public Employee getEmployee(Long employeeId) {
        return employeeRepository.getBy(employeeId);
    }

    public Money getTotalAmountOfPaidTaxes(Employee employee) {
        return monthlyTaxForEmployeeRepository.findByEmployee(employee)
                .stream()
                .filter(monthlyTaxForEmployee -> monthlyTaxForEmployee.hasErrorMessage() == false)
                .map(MonthlyTaxForEmployee::getTax)
                .reduce((a, b) -> a.plus(b))
                .orElse(Money.zero(CurrencyUnit.EUR));
    }

    public List<MonthlyTaxForEmployee> getEmployeeTaxes(Long employeeId) {
        Employee employee = employeeRepository.getBy(employeeId);
        return monthlyTaxForEmployeeRepository.findByEmployee(employee);
    }

    public Long getEmployeeCount() {
        return employeeRepository.count();
    }


}
