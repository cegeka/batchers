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

    public List<EmployeeTo> getEmployees(int page, int pageSize) {
        return employeeRepository.getEmployees(page, pageSize);
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
        payCheckRepository.deleteAll();
        taxServiceCallResultRepository.deleteAll();
        // if we use truncate we get this error
        // [SqlExceptionHelper] Cannot truncate a table referenced in a foreign key constraint
        taxCalculationRepository.deleteAll();
        employeeRepository.deleteAll();
    }

    public List<Long> getEmployeeIds(long year, long month, long jobExecutionId) {
        return employeeRepository.getEmployeeIds(year, month, jobExecutionId);
    }

    public Employee getEmployee(Long employeeId) {
        return employeeRepository.getBy(employeeId);
    }

    public List<TaxCalculation> getEmployeeTaxes(Long employeeId) {
        Employee employee = employeeRepository.getBy(employeeId);
        return taxCalculationRepository.findByEmployee(employee);
    }
}
