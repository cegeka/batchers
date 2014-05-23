package be.cegeka.batchers.taxcalculator.application.domain;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
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

    public TaxServiceCallResult findLastByTaxCalculation(TaxCalculation taxCalculation) {
        TypedQuery<TaxServiceCallResult> byTaxCalculation = entityManager.createNamedQuery(TaxServiceCallResult.FIND_LAST_BY_TAXCALCULATION, TaxServiceCallResult.class);

        byTaxCalculation.setParameter("taxCalculationId", taxCalculation.getId());

        List<TaxServiceCallResult> resultList = byTaxCalculation.getResultList();
        if (resultList.size() > 0) {
            TaxServiceCallResult taxServiceCallResult = resultList.get(0);
            return taxServiceCallResult;
        }
        return null;
    }

    public void deleteAll() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaDelete<TaxServiceCallResult> criteriaDelete = criteriaBuilder.createCriteriaDelete(TaxServiceCallResult.class);

        criteriaDelete.from(TaxServiceCallResult.class);

        entityManager.createQuery(criteriaDelete).executeUpdate();
    }
}
