package be.cegeka.batchers.taxcalculator.application.domain;

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
}
