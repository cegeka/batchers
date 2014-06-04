package be.cegeka.batchers.taxcalculator.batch.config;

import be.cegeka.batchers.taxcalculator.application.domain.EmployeeTestBuilder;
import be.cegeka.batchers.taxcalculator.application.service.TaxWebServiceNonFatalException;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RetryConfigTest {

    @InjectMocks
    private RetryConfig retryConfig;

    private RetryTemplate retryTemplate;

    @Mock
    private RetryCallback<Object, TaxWebServiceNonFatalException> retryCallback;

    @Before
    public void setUp() throws Exception {
        retryTemplate = retryConfig.createRetryTemplate();
    }

    @Test
    public void whenRetryCallbackFails_retryTimeIsExponential() throws TaxWebServiceNonFatalException {
        long start = System.currentTimeMillis();

        when(retryCallback.doWithRetry(any(RetryContext.class)))
                .thenThrow(new TaxWebServiceNonFatalException(new EmployeeTestBuilder().build(), Money.of(CurrencyUnit.EUR, 10), null, null, "boe"))
                .thenThrow(new TaxWebServiceNonFatalException(new EmployeeTestBuilder().build(), Money.of(CurrencyUnit.EUR, 10), null, null, "boe"))
                .thenReturn(any());

        retryTemplate.execute(retryCallback);

        verify(retryCallback, times(3)).doWithRetry(any(RetryContext.class));

        long end = System.currentTimeMillis();
        long duration = end - start;
        assertThat(duration).isGreaterThanOrEqualTo(300);
    }
}
