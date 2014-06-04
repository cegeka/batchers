package be.cegeka.batchers.taxcalculator.application.domain;


import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.util.List;

@Repository
@Transactional(readOnly = true, isolation = Isolation.DEFAULT)
public class TaxCalculationRepository extends AbstractRepository<TaxCalculation> {

    public TaxCalculation getBy(Long id) {
        return entityManager.find(TaxCalculation.class, id);
    }

    public List<TaxCalculation> find(int year, int month, long jobExecutionId) {
        TypedQuery<TaxCalculation> byMonthAndYear = entityManager.createNamedQuery(TaxCalculation.FIND_BY_YEAR_AND_MONTH, TaxCalculation.class);

        byMonthAndYear.setParameter("year", year);
        byMonthAndYear.setParameter("month", month);
        byMonthAndYear.setParameter("jobExecutionId", jobExecutionId);

        return byMonthAndYear.getResultList();
    }

    public List<TaxCalculation> findByEmployee(Employee employee) {
        TypedQuery<TaxCalculation> byEmployee = entityManager.createNamedQuery(TaxCalculation.FIND_BY_EMPLOYEE, TaxCalculation.class);

        byEmployee.setParameter("employeeId", employee.getId());

        return byEmployee.getResultList();
    }
}
