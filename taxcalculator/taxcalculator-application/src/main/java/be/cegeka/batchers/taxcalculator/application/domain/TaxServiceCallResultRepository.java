package be.cegeka.batchers.taxcalculator.application.domain;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import java.util.List;

@Repository
@Transactional(readOnly = true, isolation = Isolation.DEFAULT)
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

    public Money getSuccessSum(long year, long month) {
        Money sum = entityManager.createNamedQuery(TaxServiceCallResult.GET_SUCCESS_SUM, Money.class)
                .setParameter("month", month)
                .setParameter("year", year)
                .getSingleResult();

        return sum == null ? Money.zero(CurrencyUnit.EUR) : sum;
    }

    public Money getFailedSum(long year, long month) {
        Money sum = entityManager.createNamedQuery(TaxServiceCallResult.GET_FAILED_SUM, Money.class)
                .setParameter("month", month)
                .setParameter("year", year)
                .getSingleResult();

        return sum == null ? Money.zero(CurrencyUnit.EUR) : sum;
    }


    public TaxServiceCallResult findLastByTaxCalculation(TaxCalculation taxCalculation) {
        TypedQuery<TaxServiceCallResult> byTaxCalculation = entityManager.createNamedQuery(TaxServiceCallResult.FIND_LAST_BY_TAXCALCULATION, TaxServiceCallResult.class);

        byTaxCalculation.setParameter("taxCalculationId", taxCalculation.getId());
        byTaxCalculation.setMaxResults(1);

        try {
            return byTaxCalculation.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void deleteAll() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaDelete<TaxServiceCallResult> criteriaDelete = criteriaBuilder.createCriteriaDelete(TaxServiceCallResult.class);

        criteriaDelete.from(TaxServiceCallResult.class);

        entityManager.createQuery(criteriaDelete).executeUpdate();
    }
}
