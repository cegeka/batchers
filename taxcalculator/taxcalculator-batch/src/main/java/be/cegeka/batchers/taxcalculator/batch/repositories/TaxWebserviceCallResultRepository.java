package be.cegeka.batchers.taxcalculator.batch.repositories;

import be.cegeka.batchers.taxcalculator.application.domain.AbstractRepository;
import be.cegeka.batchers.taxcalculator.batch.domain.TaxCalculation;
import be.cegeka.batchers.taxcalculator.batch.domain.TaxWebserviceCallResult;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
@Transactional(readOnly = true, isolation = Isolation.DEFAULT)
public class TaxWebserviceCallResultRepository extends AbstractRepository<TaxWebserviceCallResult> {

    public List<TaxWebserviceCallResult> findByTaxCalculation(TaxCalculation taxCalculation) {
        TypedQuery<TaxWebserviceCallResult> byTaxCalculation = entityManager.createNamedQuery(TaxWebserviceCallResult.FIND_BY_TAXCALCULATION, TaxWebserviceCallResult.class);

        byTaxCalculation.setParameter("taxCalculationId", taxCalculation.getId());

        return byTaxCalculation.getResultList();
    }

    public TaxWebserviceCallResult findSuccessfulByTaxCalculation(TaxCalculation taxCalculation) {
        TypedQuery<TaxWebserviceCallResult> byTaxCalculation = entityManager.createNamedQuery(TaxWebserviceCallResult.FIND_SUCCESSFUL_BY_TAXCALCULATION, TaxWebserviceCallResult.class);

        byTaxCalculation.setParameter("taxCalculationId", taxCalculation.getId());
        byTaxCalculation.setMaxResults(1);

        try {
            return byTaxCalculation.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

}
