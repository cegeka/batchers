package be.cegeka.batchers.taxcalculator.application.domain;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Repository
public class PayCheckPdfRepository {

    @PersistenceContext
    EntityManager entityManager;

    public PayCheckPdf findByTaxCalculation(TaxCalculation taxCalculation) {
        TypedQuery<PayCheckPdf> typedQuery = entityManager.createNamedQuery(PayCheckPdf.FIND_BY_TAXCALCULATION, PayCheckPdf.class);
        typedQuery.setParameter("taxCalculationId", taxCalculation.getId());
        return typedQuery.getSingleResult();
    }

    public void save(PayCheckPdf payCheckPdf) {
        entityManager.persist(payCheckPdf);
    }
}
