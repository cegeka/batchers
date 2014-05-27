package be.cegeka.batchers.taxcalculator.batch.service;

import be.cegeka.batchers.taxcalculator.application.domain.TaxCalculation;
import be.cegeka.batchers.taxcalculator.application.domain.TaxServiceCallResult;
import be.cegeka.batchers.taxcalculator.application.domain.TaxServiceCallResultRepository;
import be.cegeka.batchers.taxcalculator.application.service.TaxWebServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.Callable;

@Service
public class TaxPaymentWebServiceFacade {

	@Autowired
	private TaxServiceCallResultRepository taxServiceCallResultRepository;

    public TaxServiceCallResult callTaxService(TaxCalculation taxCalculation, Callable<TaxServiceCallResult> callable)
            throws Exception {
        TaxServiceCallResult previousResult = taxServiceCallResultRepository.findSuccessfulByTaxCalculation(taxCalculation);

        if (previousResult != null) {
            return previousResult;
        }

        try{
            TaxServiceCallResult successTaxServiceCallResult = callable.call();
            saveTaxServiceCallResult(successTaxServiceCallResult);
            return successTaxServiceCallResult;
        } catch(TaxWebServiceException e){
            TaxServiceCallResult failedTaxServiceCallResult = e.getTaxServiceCallResult();
            saveTaxServiceCallResult(failedTaxServiceCallResult);
            throw e;
        }
    }

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private void saveTaxServiceCallResult(TaxServiceCallResult taxServiceCallResult) {
		taxServiceCallResultRepository.save(taxServiceCallResult);
	}

}
