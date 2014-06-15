package be.cegeka.batchers.taxcalculator.application.service.exceptions;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import org.joda.money.Money;
import org.springframework.http.HttpStatus;

public abstract class TaxWebServiceException extends Exception {

    private final Employee employee;
    private final Money taxesToPay;
    private final HttpStatus httpStatus;
    private final String responseBody;

    public TaxWebServiceException(Employee employee, Money taxesToPay, HttpStatus httpStatus, String responseBody, String reason) {
        super(createMessage(employee, taxesToPay, reason));
        this.employee = employee;
        this.taxesToPay = taxesToPay;
        this.httpStatus = httpStatus;
        this.responseBody = responseBody;
    }

    public TaxWebServiceException(Employee employee, Money taxesToPay, HttpStatus httpStatus, String responseBody, Throwable t) {
        super(createMessage(employee, taxesToPay, t.getMessage()), t);
        this.employee = employee;
        this.taxesToPay = taxesToPay;
        this.httpStatus = httpStatus;
        this.responseBody = responseBody;
    }

    static String createMessage(Employee employee, Money taxesToPay, String reason) {
        return String.format("Paying the taxes for employee with id %d with amount %s failed because of %s", employee.getId(), taxesToPay.toString(), reason);
    }

    public String getResponseBody() {
        return responseBody;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public Money getTaxesToPay() {
        return taxesToPay;
    }

    public Employee getEmployee() {
        return employee;
    }
}
