package be.cegeka.batchers.taxcalculator.batch.repositories;


import be.cegeka.batchers.taxcalculator.application.domain.AbstractRepository;
import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.batch.domain.TaxCalculation;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
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

    public Money getSuccessSum(int year, int month) {
        Money sum = entityManager.createNamedQuery(TaxCalculation.GET_SUCCESS_SUM, Money.class)
                .setParameter("month", month)
                .setParameter("year", year)
                .getSingleResult();

        return sum == null ? Money.zero(CurrencyUnit.EUR) : sum;
    }

    public Money getFailedSum(int year, int month) {
        Money sum = entityManager.createNamedQuery(TaxCalculation.GET_FAILED_SUM, Money.class)
                .setParameter("month", month)
                .setParameter("year", year)
                .getSingleResult();

        return sum == null ? Money.zero(CurrencyUnit.EUR) : sum;
    }
}
