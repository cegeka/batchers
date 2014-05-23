package be.cegeka.batchers.taxcalculator.batch.config;

import be.cegeka.batchers.taxcalculator.application.service.TaxWebServiceException;
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
    private RetryCallback<Object, TaxWebServiceException> retryCallback;

    @Before
    public void setUp() throws Exception {
        retryTemplate = retryConfig.createRetryTemplate();
    }

    @Test
    public void whenRetryCallbackFails_retryTimeIsExponential() {
        long start = System.currentTimeMillis();

        when(retryCallback.doWithRetry(any(RetryContext.class)))
                .thenThrow(new TaxWebServiceException("boe"))
                .thenThrow(new TaxWebServiceException("boe"))
                .thenReturn(any());

        retryTemplate.execute(retryCallback);

        verify(retryCallback, times(3)).doWithRetry(any(RetryContext.class));

        long end = System.currentTimeMillis();
        long duration = end - start;
        assertThat(duration).isGreaterThanOrEqualTo(300);
    }
}
