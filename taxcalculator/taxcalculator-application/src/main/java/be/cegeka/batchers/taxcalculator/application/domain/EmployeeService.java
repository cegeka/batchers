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
    private PayCheckRepository payCheckRepository;

    @Autowired
    private TaxCalculationRepository taxCalculationRepository;

    @Autowired
    private TaxServiceCallResultRepository taxServiceCallResultRepository;


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
        // if we use truncate we get this error
        // [SqlExceptionHelper] Cannot truncate a table referenced in a foreign key constraint
        employeeRepository.deleteAll();
        taxCalculationRepository.deleteAll();
    }
}
