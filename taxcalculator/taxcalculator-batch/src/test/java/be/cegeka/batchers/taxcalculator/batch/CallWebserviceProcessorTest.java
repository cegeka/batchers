package be.cegeka.batchers.taxcalculator.batch;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.domain.EmployeeBuilder;
import be.cegeka.batchers.taxcalculator.application.domain.TaxCalculation;
import be.cegeka.batchers.taxcalculator.application.domain.TaxServiceCallResult;
import be.cegeka.batchers.taxcalculator.application.service.TaxWebServiceException;
import be.cegeka.batchers.taxcalculator.batch.config.RetryConfig;
import be.cegeka.batchers.taxcalculator.batch.service.TaxPaymentWebServiceFacade;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.concurrent.Callable;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CallWebserviceProcessorTest {

    @InjectMocks
    private CallWebserviceProcessor callWebserviceProcessor;

    @Mock
    private TaxPaymentWebServiceFacade taxPaymentWebServiceFacade;

    @Mock
    private RetryConfig retryConfig;

    private Employee employee;
    private TaxCalculation taxCalculation;
    private TaxServiceCallResult taxServiceCallResult;

    @Before
    public void setUp() {
        employee = new EmployeeBuilder().build();
        Money money = Money.of(CurrencyUnit.EUR, 2000.0);
        taxCalculation = TaxCalculation.from(1L, employee, 2014, 1, money);
        taxServiceCallResult = TaxServiceCallResult.from(taxCalculation, "", HttpStatus.OK.value(), "", DateTime.now(), true);
    }

    @Test
    public void testProcessHappyPath_NoExceptionHasBeenThrownAndEmployeeIsReturned() throws Exception {
        when(taxPaymentWebServiceFacade.callTaxService(eq(taxCalculation), any(Callable.class))).thenReturn(taxServiceCallResult);

        TaxServiceCallResult taxServiceCallResult1 = callWebserviceProcessor.process(taxCalculation);
        assertThat(taxServiceCallResult1.getTaxCalculation().getEmployee()).isEqualTo(employee);
    }

    @Test(expected = TaxWebServiceException.class)
    public void testProcessBadResponse_ExceptionHasBeenThrownAndEmployeeIsReturned() throws Exception {
        when(taxPaymentWebServiceFacade.callTaxService(eq(taxCalculation), any(Callable.class))).thenThrow(new TaxWebServiceException("boe"));

        callWebserviceProcessor.process(taxCalculation);
    }

}
