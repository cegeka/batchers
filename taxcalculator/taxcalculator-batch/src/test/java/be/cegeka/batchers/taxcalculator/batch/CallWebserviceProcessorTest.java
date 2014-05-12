package be.cegeka.batchers.taxcalculator.batch;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.domain.EmployeeBuilder;
import be.cegeka.batchers.taxcalculator.application.service.TaxPaymentWebService;
import be.cegeka.batchers.taxcalculator.application.service.TaxWebServiceException;
import be.cegeka.batchers.taxcalculator.to.TaxTo;
import org.fest.assertions.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

@RunWith(MockitoJUnitRunner.class)
public class CallWebserviceProcessorTest {

    @InjectMocks
    private CallWebserviceProcessor callWebserviceProcessor;

    @Mock
    private TaxPaymentWebService taxPaymentWebServiceMock;

    @Test
    public void testProcessHappyPath_NoExceptionHasBeenThrownAndEmployeeIsReturned() throws Exception {
        Employee employee = new EmployeeBuilder().build();

        when(taxPaymentWebServiceMock.doWebserviceCallToTaxService(employee)).thenReturn(employee);

        assertThat(callWebserviceProcessor.process(employee)).isEqualTo(employee);
    }

    @Test(expected = TaxWebServiceException.class)
    public void testProcessBadResponse_ExceptionHasBeenThrownAndEmployeeIsReturned() throws Exception {
        Employee employee = new EmployeeBuilder().build();

        when(taxPaymentWebServiceMock.doWebserviceCallToTaxService(employee)).thenThrow(new TaxWebServiceException("boe"));

        callWebserviceProcessor.process(employee);
    }

    @Test
    public void testProcessBadThenGoodResponse_RetryAndEmployeeIsReturned() throws Exception {
        Employee employee = new EmployeeBuilder().build();

        when(taxPaymentWebServiceMock.doWebserviceCallToTaxService(employee))
            .thenThrow(new TaxWebServiceException("boe"))
            .thenReturn(employee);

        assertThat(callWebserviceProcessor.process(employee)).isEqualTo(employee);
    }

    @Test
    public void testProcessExponential_RetryAndEmployeeIsReturned() throws Exception {
        long start = System.currentTimeMillis();
        Employee employee = new EmployeeBuilder().build();
        when(taxPaymentWebServiceMock.doWebserviceCallToTaxService(employee))
            .thenThrow(new TaxWebServiceException("boe"))
            .thenThrow(new TaxWebServiceException("boe"))
            .thenReturn(employee);

        Employee processed = callWebserviceProcessor.process(employee);

        assertThat(processed).isEqualTo(employee);
        verify(taxPaymentWebServiceMock, times(3)).doWebserviceCallToTaxService(employee);

        long end = System.currentTimeMillis();
        long duration = end - start;
        assertThat(duration).isGreaterThanOrEqualTo(300);
    }
}
