package be.cegeka.batchers.taxcalculator.application.service;

import be.cegeka.batchers.taxcalculator.application.domain.TaxServiceCallResult;

public class TaxWebServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private TaxServiceCallResult taxServiceCallResult;

    public TaxWebServiceException(String message) {
        super(message);
    }

    public TaxWebServiceException(TaxServiceCallResult taxServiceCallResult) {
        super();
        this.taxServiceCallResult = taxServiceCallResult;
    }

    public TaxServiceCallResult getTaxServiceCallResult() {
        return taxServiceCallResult;
    }
}
