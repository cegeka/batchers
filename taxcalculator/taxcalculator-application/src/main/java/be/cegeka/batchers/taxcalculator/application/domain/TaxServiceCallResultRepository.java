package be.cegeka.batchers.taxcalculator.application.domain;

import org.joda.money.CurrencyUnit;
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

        Double sum = entityManager.createNamedQuery(TaxServiceCallResult.GET_SUCCESS_SUM, Double.class)
                .setParameter("month", month)
                .setParameter("year", year)
                .getSingleResult();

        return Money.of(CurrencyUnit.EUR, sum);
    }
}
