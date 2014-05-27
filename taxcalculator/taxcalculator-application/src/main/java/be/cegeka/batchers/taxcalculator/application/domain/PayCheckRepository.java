package be.cegeka.batchers.taxcalculator.application.domain;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;

@Repository
@Transactional(readOnly = true, isolation = Isolation.DEFAULT)
public class PayCheckRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void save(PayCheck payCheckPdf) {
        entityManager.persist(payCheckPdf);
    }

    public PayCheck findByTaxCalculation(TaxCalculation taxCalculation) {
        TypedQuery<PayCheck> typedQuery = entityManager.createNamedQuery(PayCheck.FIND_BY_TAXCALCULATION, PayCheck.class);
        typedQuery.setParameter("taxCalculationId", taxCalculation.getId());
        return typedQuery.getSingleResult();
    }

    public void deleteAll() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaDelete<PayCheck> criteriaDelete = criteriaBuilder.createCriteriaDelete(PayCheck.class);

        criteriaDelete.from(PayCheck.class);

        entityManager.createQuery(criteriaDelete).executeUpdate();
    }
}
