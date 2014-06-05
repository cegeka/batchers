package be.cegeka.batchers.taxcalculator.application.service;


import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import org.joda.money.Money;
import org.springframework.http.HttpStatus;

public class TaxWebServiceFatalException extends TaxWebServiceException {


    public TaxWebServiceFatalException(Employee employee, Money taxesToPay, HttpStatus httpStatus, String responseBody, String reason) {
        super(employee, taxesToPay, httpStatus, responseBody, reason);
    }

    public TaxWebServiceFatalException(Employee employee, Money taxesToPay, HttpStatus httpStatus, String responseBody, Throwable t) {
        super(employee, taxesToPay, httpStatus, responseBody, t);
    }

}
