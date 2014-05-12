package be.cegeka.batchers.taxcalculator.application.service;

public class TaxWebServiceException extends RuntimeException {

    public TaxWebServiceException(String message) {
        super(message);
    }

    public TaxWebServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
