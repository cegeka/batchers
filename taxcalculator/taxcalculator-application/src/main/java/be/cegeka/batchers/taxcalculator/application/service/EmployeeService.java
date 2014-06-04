package be.cegeka.batchers.taxcalculator.application.service;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.repositories.EmployeeRepository;
import be.cegeka.batchers.taxcalculator.application.domain.MonthlyTaxForEmployee;
import be.cegeka.batchers.taxcalculator.application.repositories.MonthlyTaxForEmployeeRepository;
import be.cegeka.batchers.taxcalculator.application.repositories.MonthlyReportRepository;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private MonthlyReportRepository monthlyReportRepository;

    @Autowired
    private MonthlyTaxForEmployeeRepository monthlyTaxForEmployeeRepository;

    public List<Employee> getEmployees(int page, int pageSize) {
        return employeeRepository.getEmployees(page, pageSize);
    }

    public Money getTotalAmountOfPaidTaxes(Employee employee) {
        return monthlyTaxForEmployeeRepository.findByEmployee(employee)
                .stream()
                .filter(monthlyTaxForEmployee -> monthlyTaxForEmployee.hasErrorMessage() == false)
                .map(MonthlyTaxForEmployee::getTax)
                .reduce((a, b) -> a.plus(b))
                .orElse(Money.zero(CurrencyUnit.EUR));
    }

    public long getEmployeeCount() {
        return employeeRepository.getEmployeeCount();
    }

    public Long count() {
        return employeeRepository.count();
    }

    @Transactional
    public void deleteAll() {
        monthlyReportRepository.deleteAll();
        employeeRepository.deleteAll();
    }
}
