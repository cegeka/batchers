package be.cegeka.batchers.taxcalculator.batch.service;

import be.cegeka.batchers.taxcalculator.application.service.exceptions.TaxWebServiceNonFatalException;
import be.cegeka.batchers.taxcalculator.batch.domain.TaxCalculation;
import be.cegeka.batchers.taxcalculator.batch.domain.TaxWebserviceCallResult;
import be.cegeka.batchers.taxcalculator.batch.domain.TaxWebserviceCallResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.Callable;

@Service
public class TaxPaymentWebServiceFacade {

    @Autowired
    private TaxWebserviceCallResultRepository taxWebserviceCallResultRepository;

    public TaxWebserviceCallResult callTaxService(TaxCalculation taxCalculation, Callable<Void> callable) throws Exception {
        TaxWebserviceCallResult previousResult = taxWebserviceCallResultRepository.findSuccessfulByTaxCalculation(taxCalculation);

        if (previousResult != null) {
            return previousResult;
        }

        try {
            callable.call();
            TaxWebserviceCallResult successTaxWebserviceCallResult = TaxWebserviceCallResult.callSucceeded(taxCalculation);
            saveTaxServiceCallResult(successTaxWebserviceCallResult);
            return successTaxWebserviceCallResult;
        } catch (TaxWebServiceNonFatalException e) {
            TaxWebserviceCallResult failedTaxWebserviceCallResult = TaxWebserviceCallResult.callFailed(taxCalculation, e);
            saveTaxServiceCallResult(failedTaxWebserviceCallResult);
            throw e;
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void saveTaxServiceCallResult(TaxWebserviceCallResult taxWebserviceCallResult) {
        taxWebserviceCallResultRepository.save(taxWebserviceCallResult);
    }

}
