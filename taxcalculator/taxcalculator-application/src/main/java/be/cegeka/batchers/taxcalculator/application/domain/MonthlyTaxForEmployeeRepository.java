package be.cegeka.batchers.taxcalculator.application.domain;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.util.List;

@Repository
@Transactional(readOnly = false, isolation = Isolation.DEFAULT)
public class MonthlyTaxForEmployeeRepository extends AbstractRepository<MonthlyTaxForEmployee> {

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.DEFAULT)
    public MonthlyTaxForEmployee save(MonthlyTaxForEmployee entity) {
        MonthlyTaxForEmployee monthlyTaxForEmployee = find(entity.getEmployee(), entity.getYear(), entity.getMonth());
        if (monthlyTaxForEmployee != null) {
            monthlyTaxForEmployee.setLastErrorMessage(entity.getLastErrorMessage());
            monthlyTaxForEmployee.setMonthlyReportPdf(entity.getMonthlyReportPdf());
            monthlyTaxForEmployee.setCalculationDate(entity.getCalculationDate());
            return monthlyTaxForEmployee;
        } else {
            return super.save(entity);
        }
    }

    public List<MonthlyTaxForEmployee> findByEmployee(Employee employee) {
        TypedQuery<MonthlyTaxForEmployee> typedQuery = entityManager.createNamedQuery(MonthlyTaxForEmployee.FIND_BY_EMPLOYEE, MonthlyTaxForEmployee.class);

        typedQuery.setParameter("employeeId", employee.getId());

        return typedQuery.getResultList();
    }

    public MonthlyTaxForEmployee find(Employee employee, int year, int month) {
        TypedQuery<MonthlyTaxForEmployee> typedQuery = entityManager.createNamedQuery(MonthlyTaxForEmployee.FIND_BY_EMPLOYEE_YEAR_AND_MONTH, MonthlyTaxForEmployee.class);

        typedQuery.setParameter("employeeId", employee.getId());
        typedQuery.setParameter("year", year);
        typedQuery.setParameter("month", month);

        return typedQuery.getResultList()
                .stream()
                .findAny()
                .orElse(null);
    }
}
