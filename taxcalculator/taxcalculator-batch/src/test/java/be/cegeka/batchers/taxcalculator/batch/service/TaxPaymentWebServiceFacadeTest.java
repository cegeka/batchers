package be.cegeka.batchers.taxcalculator.batch.service;

import be.cegeka.batchers.taxcalculator.application.domain.TaxCalculation;
import be.cegeka.batchers.taxcalculator.application.domain.TaxCalculationTestBuilder;
import be.cegeka.batchers.taxcalculator.application.domain.TaxServiceCallResult;
import be.cegeka.batchers.taxcalculator.application.domain.TaxServiceCallResultRepository;
import be.cegeka.batchers.taxcalculator.application.service.TaxWebServiceException;
import org.hamcrest.Matchers;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

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
    private Callable<TaxServiceCallResult> taxServiceCallResultCallable;

    @Mock
    private TaxServiceCallResultRepository taxServiceCallResultRepository;

    private TaxCalculation taxCalculation;

    private TaxServiceCallResult taxServiceCallResultValid;

    protected void expectExceptionWithMessage(Class<? extends Throwable> exception, String message) {
        thrown.expect(Matchers.allOf(Matchers.instanceOf(exception), Matchers.hasProperty("message", equalTo(message))));
    }

    @Before
    public void setUp() {
        Money money = Money.of(CurrencyUnit.EUR, 2000.0);
        taxCalculation = new TaxCalculationTestBuilder().withTax(money).build();
        taxServiceCallResultValid = TaxServiceCallResult.from(taxCalculation, "", HttpStatus.OK.value(), "", DateTime.now(), true);
    }

    @Test
    public void whenPreviousCallResultIsFailed_andCallReturnsValid_callResultIsReturned() throws Exception {
        when(taxServiceCallResultRepository.findSuccessfulByTaxCalculation(taxCalculation)).thenReturn(null);
        when(taxServiceCallResultCallable.call()).thenReturn(taxServiceCallResultValid);

        assertThat(taxPaymentWebServiceFacade.callTaxService(taxCalculation, taxServiceCallResultCallable))
                .isEqualTo(taxServiceCallResultValid);
    }

    @Test
    public void whenPreviousCallResultIsFailed_andCallReturnsException_exceptionIsReturned() throws Exception {
        when(taxServiceCallResultRepository.findSuccessfulByTaxCalculation(taxCalculation)).thenReturn(null);
        when(taxServiceCallResultCallable.call()).thenThrow(new TaxWebServiceException("some message"));

        expectExceptionWithMessage(TaxWebServiceException.class, "some message");

        taxPaymentWebServiceFacade.callTaxService(taxCalculation, taxServiceCallResultCallable);
    }

    @Test
    public void whenPreviousCallResultIsValid_thenPreviousCallResultIsReturned() throws Exception {
        when(taxServiceCallResultRepository.findSuccessfulByTaxCalculation(taxCalculation)).thenReturn(taxServiceCallResultValid);
        verify(taxServiceCallResultCallable, never()).call();

        assertThat(taxPaymentWebServiceFacade.callTaxService(taxCalculation, taxServiceCallResultCallable))
                .isEqualTo(taxServiceCallResultValid);
    }


}
