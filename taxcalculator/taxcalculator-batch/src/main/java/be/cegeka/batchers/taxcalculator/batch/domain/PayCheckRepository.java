package be.cegeka.batchers.taxcalculator.batch.domain;

import be.cegeka.batchers.taxcalculator.application.domain.AbstractRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;

@Repository
@Transactional(readOnly = true, isolation = Isolation.DEFAULT)
public class PayCheckRepository extends AbstractRepository<PayCheck> {

    public PayCheck findByTaxCalculation(TaxCalculation taxCalculation) {
        TypedQuery<PayCheck> typedQuery = entityManager.createNamedQuery(PayCheck.FIND_BY_TAXCALCULATION, PayCheck.class);
        typedQuery.setParameter("taxCalculationId", taxCalculation.getId());
        return typedQuery.getSingleResult();
    }

}
