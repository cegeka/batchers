package be.cegeka.batchers.taxcalculator.application.service;


import be.cegeka.batchers.taxcalculator.application.domain.TaxServiceCallResult;

public class TaxWebServiceFatalException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private TaxServiceCallResult taxServiceCallResult;

    public TaxWebServiceFatalException(String message) {
        super(message);
    }

    public TaxWebServiceFatalException(String message, TaxServiceCallResult taxServiceCallResult) {
        super(message);
        this.taxServiceCallResult = taxServiceCallResult;
    }

    public TaxServiceCallResult getTaxServiceCallResult() {
        return taxServiceCallResult;
    }
}
