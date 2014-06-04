package be.cegeka.batchers.taxcalculator.batch.service;

import be.cegeka.batchers.taxcalculator.application.domain.EmployeeTestBuilder;
import be.cegeka.batchers.taxcalculator.application.service.TaxWebServiceNonFatalException;
import be.cegeka.batchers.taxcalculator.batch.domain.TaxCalculation;
import be.cegeka.batchers.taxcalculator.batch.domain.TaxCalculationTestBuilder;
import be.cegeka.batchers.taxcalculator.batch.domain.TaxWebserviceCallResult;
import be.cegeka.batchers.taxcalculator.batch.repositories.TaxWebserviceCallResultRepository;
import org.hamcrest.Matchers;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.Callable;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TaxPaymentWebServiceFacadeTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @InjectMocks
    private TaxPaymentWebServiceFacade taxPaymentWebServiceFacade;

    @Mock
    private Callable<Void> taxServiceCallResultCallable;

    @Mock
    private TaxWebserviceCallResultRepository taxWebserviceCallResultRepository;

    private TaxCalculation taxCalculation;

    private TaxWebserviceCallResult taxWebserviceCallResultValid;

    protected void expectExceptionWithMessage(Class<? extends Throwable> exception, String message) {
        thrown.expect(Matchers.allOf(Matchers.instanceOf(exception), Matchers.hasProperty("message", equalTo(message))));
    }

    @Before
    public void setUp() {
        Money money = Money.of(CurrencyUnit.EUR, 2000.0);
        taxCalculation = new TaxCalculationTestBuilder().withTax(money).build();
        taxWebserviceCallResultValid = TaxWebserviceCallResult.callSucceeded(taxCalculation);
    }

    @Test
    public void whenPreviousCallResultIsFailed_andCallReturnsValid_callResultIsReturned() throws Exception {
        when(taxWebserviceCallResultRepository.findSuccessfulByTaxCalculation(taxCalculation)).thenReturn(null);
        when(taxServiceCallResultCallable.call()).thenReturn(null);

        assertThat(taxPaymentWebServiceFacade.callTaxService(taxCalculation, taxServiceCallResultCallable))
                .isEqualTo(taxWebserviceCallResultValid);
    }

    @Test
    public void whenPreviousCallResultIsFailed_andCallReturnsException_exceptionIsReturned() throws Exception {
        when(taxWebserviceCallResultRepository.findSuccessfulByTaxCalculation(taxCalculation)).thenReturn(null);
        when(taxServiceCallResultCallable.call()).thenThrow(new TaxWebServiceNonFatalException(new EmployeeTestBuilder().build(), Money.of(CurrencyUnit.EUR, 10), null, null, "boe"));

        expectExceptionWithMessage(TaxWebServiceNonFatalException.class, "some message");

        taxPaymentWebServiceFacade.callTaxService(taxCalculation, taxServiceCallResultCallable);
    }

    @Test
    public void whenPreviousCallResultIsValid_thenPreviousCallResultIsReturned() throws Exception {
        when(taxWebserviceCallResultRepository.findSuccessfulByTaxCalculation(taxCalculation)).thenReturn(taxWebserviceCallResultValid);
        verify(taxServiceCallResultCallable, never()).call();

        assertThat(taxPaymentWebServiceFacade.callTaxService(taxCalculation, taxServiceCallResultCallable))
                .isEqualTo(taxWebserviceCallResultValid);
    }


}
