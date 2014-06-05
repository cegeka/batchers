package be.cegeka.batchers.taxcalculator.application.domain.generation;

import be.cegeka.batchers.taxcalculator.application.domain.EmployeeRepository;
import be.cegeka.batchers.taxcalculator.application.domain.MonthlyTaxForEmployeeRepository;
import be.cegeka.batchers.taxcalculator.application.domain.reporting.MonthlyReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(value=99)
public class ApplicationRepositoryCleaner implements EmployeeGeneratorCleaner {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private MonthlyReportRepository monthlyReportRepository;

    @Autowired
    private MonthlyTaxForEmployeeRepository monthlyTaxForEmployeeRepository;

    public void deleteAll() {
        monthlyTaxForEmployeeRepository.deleteAll();
        monthlyReportRepository.deleteAll();
        employeeRepository.deleteAll();
    }
}
