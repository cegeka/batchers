package be.cegeka.batchers.taxcalculator.application.domain;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Repository
public class PayCheckRepository {

    @PersistenceContext
    EntityManager entityManager;

    public void save(PayCheck payCheckPdf) {
        entityManager.persist(payCheckPdf);
    }

    public PayCheck findByTaxCalculation(TaxCalculation taxCalculation) {
        TypedQuery<PayCheck> typedQuery = entityManager.createNamedQuery(PayCheck.FIND_BY_TAXCALCULATION, PayCheck.class);
        typedQuery.setParameter("taxCalculationId", taxCalculation.getId());
        return typedQuery.getSingleResult();
    }
}
