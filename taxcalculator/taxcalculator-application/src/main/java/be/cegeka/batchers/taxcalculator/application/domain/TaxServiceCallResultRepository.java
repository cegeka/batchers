package be.cegeka.batchers.taxcalculator.application.domain;

import org.joda.money.Money;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class TaxServiceCallResultRepository {

    @PersistenceContext
    EntityManager entityManager;

    public void save(TaxServiceCallResult taxServiceCallResult) {
        entityManager.persist(taxServiceCallResult);
    }

    public List<TaxServiceCallResult> findByTaxCalculation(TaxCalculation taxCalculation) {
        TypedQuery<TaxServiceCallResult> byTaxCalculation = entityManager.createNamedQuery(TaxServiceCallResult.FIND_BY_TAXCALCULATION, TaxServiceCallResult.class);

        byTaxCalculation.setParameter("taxCalculationId", taxCalculation.getId());

        return byTaxCalculation.getResultList();
    }

    public Money getSuccessSum(int year, int month) {
        Money sum = entityManager.createNamedQuery(TaxServiceCallResult.GET_SUCCESS_SUM, Money.class)
                .setParameter("month", month)
                .setParameter("year", year)
                .getSingleResult();

        return sum;
    }

    public Money getFailedSum(int year, int month) {
        return entityManager.createNamedQuery(TaxServiceCallResult.GET_FAILED_SUM, Money.class)
                .setParameter("month", month)
                .setParameter("year", year)
                .getSingleResult();
    }
}
