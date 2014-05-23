package be.cegeka.batchers.taxcalculator.batch.service;

import be.cegeka.batchers.taxcalculator.application.domain.*;
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

import java.util.concurrent.Callable;

import static org.fest.assertions.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TaxPaymentWebServiceFacadeTest {

    @InjectMocks
    private TaxPaymentWebServiceFacade taxPaymentWebServiceFacade;

    @Mock
    private Callable<TaxServiceCallResult> taxServiceCallResultCallable;

    @Mock
    private TaxServiceCallResultRepository taxServiceCallResultRepository;

    private Employee employee;
    private TaxCalculation taxCalculation;
    private DateTime now;
    private TaxServiceCallResult taxServiceCallResultValid;
    private TaxServiceCallResult taxServiceCallResultFailed;

    @Before
    public void setUp() {
        employee = new EmployeeBuilder().build();
        now = DateTime.now();
        Money money = Money.of(CurrencyUnit.EUR, 2000.0);
        taxCalculation = TaxCalculation.from(1L, employee, 2014, 1, money);
        taxServiceCallResultValid = TaxServiceCallResult.from(taxCalculation, "", HttpStatus.OK.value(), "", now);
        taxServiceCallResultFailed = TaxServiceCallResult.from(taxCalculation, "", HttpStatus.NOT_FOUND.value(), "", now);
    }

    @Test
    public void testCallGoodResponse_callResultIsReturned() throws Exception {
        when(taxServiceCallResultRepository.findLastByTaxCalculation(taxCalculation)).thenReturn(null);
        when(taxServiceCallResultCallable.call()).thenReturn(taxServiceCallResultValid);

        assertThat(taxPaymentWebServiceFacade.callTaxService(taxCalculation, taxServiceCallResultCallable))
                .isEqualTo(taxServiceCallResultValid);
    }

    @Test(expected = TaxWebServiceException.class)
    public void whenCall_throwsException_exceptionIsReturned() throws Exception {
        when(taxServiceCallResultRepository.findLastByTaxCalculation(taxCalculation)).thenReturn(null);
        when(taxServiceCallResultCallable.call()).thenThrow(new TaxWebServiceException("some message"));

        taxPaymentWebServiceFacade.callTaxService(taxCalculation, taxServiceCallResultCallable);
    }

    @Test(expected = TaxWebServiceException.class)
    public void whenPreviousCallResultIsFailed_andCallReturnsException_exceptionIsReturned() throws Exception {
        when(taxServiceCallResultRepository.findLastByTaxCalculation(taxCalculation)).thenReturn(taxServiceCallResultFailed);
        when(taxServiceCallResultCallable.call()).thenThrow(new TaxWebServiceException("some message"));

        taxPaymentWebServiceFacade.callTaxService(taxCalculation, taxServiceCallResultCallable);
    }

    @Test
    public void whenPreviousCallResultIsFailed_andCallReturnsValid_callResultIsReturned() throws Exception {
        when(taxServiceCallResultRepository.findLastByTaxCalculation(taxCalculation)).thenReturn(taxServiceCallResultFailed);
        when(taxServiceCallResultCallable.call()).thenReturn(taxServiceCallResultValid);

        assertThat(taxPaymentWebServiceFacade.callTaxService(taxCalculation, taxServiceCallResultCallable))
                .isEqualTo(taxServiceCallResultValid);
    }

    @Test
    public void whenPreviousCallResultIsValid_thenPreviousCallResultIsReturned() throws Exception {
        when(taxServiceCallResultRepository.findLastByTaxCalculation(taxCalculation)).thenReturn(taxServiceCallResultValid);

        assertThat(taxPaymentWebServiceFacade.callTaxService(taxCalculation, taxServiceCallResultCallable))
                .isEqualTo(taxServiceCallResultValid);
    }



}
