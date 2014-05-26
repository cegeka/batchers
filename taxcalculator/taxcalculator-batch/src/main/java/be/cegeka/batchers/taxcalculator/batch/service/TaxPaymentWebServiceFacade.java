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
        TaxServiceCallResult previousResult = getPreviousTaxCalculation(taxCalculation);
        if (webServiceHasBeenCalledSuccessfully(previousResult)) {
            return previousResult;
        } else {
            TaxServiceCallResult taxServiceCallResult;
            try{
                taxServiceCallResult = callable.call();
                saveTaxServiceCallResult(taxServiceCallResult);
            } catch(TaxWebServiceException e){
                taxServiceCallResult = e.getTaxServiceCallResult();
                saveTaxServiceCallResult(taxServiceCallResult);
                throw new TaxWebServiceException(taxServiceCallResult);
            }
            return taxServiceCallResult;
        }
    }

	private TaxServiceCallResult getPreviousTaxCalculation(TaxCalculation taxCalculation) {
		TaxServiceCallResult byTaxCalculation = taxServiceCallResultRepository.findLastByTaxCalculation(taxCalculation);
		return byTaxCalculation;
	}

	private boolean webServiceHasBeenCalledSuccessfully(TaxServiceCallResult previousResult) {
		if (previousResult != null && previousResult.isSuccessfulResponse()) {
			return true;
		}
		return false;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private void saveTaxServiceCallResult(TaxServiceCallResult taxServiceCallResult) {
		taxServiceCallResultRepository.save(taxServiceCallResult);
	}

}
