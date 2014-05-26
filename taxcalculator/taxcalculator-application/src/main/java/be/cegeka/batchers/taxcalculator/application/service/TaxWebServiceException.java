package be.cegeka.batchers.taxcalculator.application.service;

public class TaxWebServiceException extends RuntimeException {

	private static final long serialVersionUID = -2385006122924525208L;

	public TaxWebServiceException(String message) {
		super(message);
	}

	public TaxWebServiceException(String message, Throwable cause) {
		super(message, cause);
	}
}
