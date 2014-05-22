package be.cegeka.batchers.taxcalculator.batch;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.domain.EmployeeBuilder;
import be.cegeka.batchers.taxcalculator.application.domain.TaxCalculation;
import be.cegeka.batchers.taxcalculator.application.domain.TaxServiceCallResult;
import be.cegeka.batchers.taxcalculator.application.service.TaxPaymentWebService;
import be.cegeka.batchers.taxcalculator.application.service.TaxWebServiceException;
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

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CallWebserviceProcessorTest {

    @InjectMocks
    private CallWebserviceProcessor callWebserviceProcessor;

    @Mock
    private TaxPaymentWebService taxPaymentWebServiceMock;

    private Employee employee;

    private TaxCalculation taxCalculation;

    private DateTime now;
    private TaxServiceCallResult taxServiceCallResult;

    @Before
    public void setUp() {
        employee = new EmployeeBuilder().build();
        now = DateTime.now();
        Money money = Money.of(CurrencyUnit.EUR, 2000.0);
        taxCalculation = TaxCalculation.from(employee, 2014, 1, money, now);
        taxServiceCallResult = TaxServiceCallResult.from(taxCalculation, "", HttpStatus.OK.value(), "", now);
    }

    @Test
    public void testProcessHappyPath_NoExceptionHasBeenThrownAndEmployeeIsReturned() throws Exception {
        when(taxPaymentWebServiceMock.doWebserviceCallToTaxService(taxCalculation)).thenReturn(taxServiceCallResult);

        TaxServiceCallResult taxServiceCallResult1 = callWebserviceProcessor.process(taxCalculation);
        assertThat(taxServiceCallResult1.getTaxCalculation().getEmployee()).isEqualTo(employee);
    }

    @Test(expected = TaxWebServiceException.class)
    public void testProcessBadResponse_ExceptionHasBeenThrownAndEmployeeIsReturned() throws Exception {
        when(taxPaymentWebServiceMock.doWebserviceCallToTaxService(taxCalculation))
                .thenThrow(new TaxWebServiceException("boe"));

        callWebserviceProcessor.process(taxCalculation);
    }

    @Test
    public void testProcessBadThenGoodResponse_RetryAndEmployeeIsReturned() throws Exception {
        when(taxPaymentWebServiceMock.doWebserviceCallToTaxService(taxCalculation))
                .thenThrow(new TaxWebServiceException("boe"))
                .thenReturn(taxServiceCallResult);

        assertThat(callWebserviceProcessor.process(taxCalculation)).isEqualTo(taxServiceCallResult);
    }

    @Test
    public void testProcessExponential_RetryAndEmployeeIsReturned() throws Exception {
        long start = System.currentTimeMillis();

        when(taxPaymentWebServiceMock.doWebserviceCallToTaxService(taxCalculation))
                .thenThrow(new TaxWebServiceException("boe"))
                .thenThrow(new TaxWebServiceException("boe"))
                .thenReturn(taxServiceCallResult);

        TaxServiceCallResult processed = callWebserviceProcessor.process(taxCalculation);

        assertThat(processed).isEqualTo(taxServiceCallResult);
        verify(taxPaymentWebServiceMock, times(3)).doWebserviceCallToTaxService(taxCalculation);

        long end = System.currentTimeMillis();
        long duration = end - start;
        assertThat(duration).isGreaterThanOrEqualTo(300);
    }

}
