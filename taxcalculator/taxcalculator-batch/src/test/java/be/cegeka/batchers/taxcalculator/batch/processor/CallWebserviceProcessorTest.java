package be.cegeka.batchers.taxcalculator.batch.processor;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.domain.EmployeeTestBuilder;
import be.cegeka.batchers.taxcalculator.application.service.TaxWebServiceNonFatalException;
import be.cegeka.batchers.taxcalculator.batch.config.RetryConfig;
import be.cegeka.batchers.taxcalculator.batch.domain.TaxCalculation;
import be.cegeka.batchers.taxcalculator.batch.domain.TaxCalculationTestBuilder;
import be.cegeka.batchers.taxcalculator.batch.domain.TaxWebserviceCallResult;
import be.cegeka.batchers.taxcalculator.batch.service.TaxPaymentWebServiceFacade;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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
    private TaxWebserviceCallResult taxWebserviceCallResult;

    @Before
    public void setUp() {
        employee = new EmployeeTestBuilder().build();
        Money money = Money.of(CurrencyUnit.EUR, 2000.0);
        taxCalculation = new TaxCalculationTestBuilder().withEmployee(employee).withTax(money).build();
        taxWebserviceCallResult = TaxWebserviceCallResult.callSucceeded(taxCalculation);
    }

    @Test
    public void testProcessHappyPath_NoExceptionHasBeenThrownAndEmployeeIsReturned() throws Exception {
        when(taxPaymentWebServiceFacade.callTaxService(eq(taxCalculation), any(Callable.class))).thenReturn(taxWebserviceCallResult);

        TaxWebserviceCallResult taxWebserviceCallResult1 = callWebserviceProcessor.process(taxCalculation);
        assertThat(taxWebserviceCallResult1.getTaxCalculation().getEmployee()).isEqualTo(employee);
    }

    @Test(expected = TaxWebServiceNonFatalException.class)
    public void testProcessBadResponse_ExceptionHasBeenThrownAndEmployeeIsReturned() throws Exception {
        when(taxPaymentWebServiceFacade.callTaxService(eq(taxCalculation), any(Callable.class))).thenThrow(new TaxWebServiceNonFatalException(new EmployeeTestBuilder().build(), Money.of(CurrencyUnit.EUR, 10), null, null, "boe"));

        callWebserviceProcessor.process(taxCalculation);
    }

}
