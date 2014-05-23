package be.cegeka.batchers.taxcalculator.batch.service;

import be.cegeka.batchers.taxcalculator.application.domain.TaxCalculation;
import be.cegeka.batchers.taxcalculator.application.domain.TaxServiceCallResult;
import be.cegeka.batchers.taxcalculator.application.domain.TaxServiceCallResultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.concurrent.Callable;

@Service
public class TaxPaymentWebServiceFacade {

    private static final Logger LOG = LoggerFactory.getLogger(TaxPaymentWebServiceFacade.class);

    @Autowired
    private TaxServiceCallResultRepository taxServiceCallResultRepository;

    public TaxServiceCallResult callTaxService(TaxCalculation taxCalculation, Callable<TaxServiceCallResult> callable)
            throws Exception {
        TaxServiceCallResult previousResult = getPreviousTaxCalculation(taxCalculation);
        if (webServiceHasBeenCalledSuccessfully(previousResult)) {
            return previousResult;
        } else {
            TaxServiceCallResult taxServiceCallResult = callable.call();
            saveTaxServiceCallResult(taxServiceCallResult);
            return taxServiceCallResult;
        }
    }

    private TaxServiceCallResult getPreviousTaxCalculation(TaxCalculation taxCalculation) {
        TaxServiceCallResult byTaxCalculation = taxServiceCallResultRepository.findLastByTaxCalculation(taxCalculation);
        return byTaxCalculation;
    }

    private boolean webServiceHasBeenCalledSuccessfully(TaxServiceCallResult previousResult) {
        if (previousResult != null && previousResult.getResponseStatus() == HttpStatus.OK.value()) {
            return true;
        }
        return false;
    }

    private void saveTaxServiceCallResult(TaxServiceCallResult taxServiceCallResult) {
        taxServiceCallResultRepository.save(taxServiceCallResult);
    }

}
