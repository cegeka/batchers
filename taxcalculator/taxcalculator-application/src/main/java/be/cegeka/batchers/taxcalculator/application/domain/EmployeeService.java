package be.cegeka.batchers.taxcalculator.application.domain;

import be.cegeka.batchers.taxcalculator.to.EmployeeTo;
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
    PayCheckRepository payCheckRepository;

    @Autowired
    TaxCalculationRepository taxCalculationRepository;

    @Autowired
    TaxServiceCallResultRepository taxServiceCallResultRepository;


    public List<EmployeeTo> getFirst20() {
        return employeeRepository.getFirst20();
    }

    public Long count() {
        return employeeRepository.count();
    }

    @Transactional
    public void truncate() {
        monthlyReportRepository.truncate();
        payCheckRepository.truncate();
        taxServiceCallResultRepository.truncate();
        taxCalculationRepository.truncate();
        employeeRepository.truncate();
    }
}
