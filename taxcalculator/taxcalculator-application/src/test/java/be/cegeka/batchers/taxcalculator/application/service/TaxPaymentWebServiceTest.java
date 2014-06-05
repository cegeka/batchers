package be.cegeka.batchers.taxcalculator.application.service;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.domain.EmployeeTestBuilder;
import be.cegeka.batchers.taxcalculator.to.TaxServiceResponse;
import be.cegeka.batchers.taxcalculator.to.TaxTo;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.SocketTimeoutException;
import java.net.URI;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

@RunWith(MockitoJUnitRunner.class)
public class TaxPaymentWebServiceTest {

    public static final String HTTP_SOMEHOST_SOMEURL = "https://www.google.com/";

    @InjectMocks
    private TaxPaymentWebService taxPaymentWebService;

    @Mock
    private RestTemplate restTemplateMock;

    @Mock
    private ResponseEntity<TaxServiceResponse> mockedResponse;

    @Before
    public void setUpCallWebserviceProcessor() {
        setInternalState(taxPaymentWebService, "taxServiceUrl", HTTP_SOMEHOST_SOMEURL);
    }

    @Test
    public void testProcessHappyPath_NoExceptionHasBeenThrownAndEmployeeIsReturned() throws Exception {
        whenCallingTheWebservice().thenReturn(mockedResponse);
        when(mockedResponse.getBody()).thenReturn(new TaxServiceResponse("OK"));
        when(mockedResponse.getStatusCode()).thenReturn(HttpStatus.OK);

        Employee employee = new EmployeeTestBuilder().build();

        taxPaymentWebService.doWebserviceCallToTaxService(employee, Money.of(CurrencyUnit.EUR, 2000.0));

        assertThat("no error has been thrown").isNotEmpty();
    }

    @Test(expected = TaxWebServiceNonFatalException.class)
    public void testProcessBadResponse_ExceptionHasBeenThrownForever() throws Exception {
        whenCallingTheWebservice().thenReturn(mockedResponse);
        when(mockedResponse.getBody()).thenReturn(new TaxServiceResponse("ERROR"));
        when(mockedResponse.getStatusCode()).thenReturn(HttpStatus.OK);

        Employee employee = new EmployeeTestBuilder().build();

        taxPaymentWebService.doWebserviceCallToTaxService(employee, Money.of(CurrencyUnit.EUR, 2000.0));
    }

    @Test(expected = TaxWebServiceNonFatalException.class)
    public void testProcessTimeoutResponse_ExceptionHasBeenThrownForever() throws Exception {
        whenCallingTheWebservice().thenThrow(aWrappedTimeOutException());

        Employee employee = new EmployeeTestBuilder().build();

        taxPaymentWebService.doWebserviceCallToTaxService(employee, Money.of(CurrencyUnit.EUR, 2000.0));
    }

    @Test(expected = TaxWebServiceNonFatalException.class)
    public void testProcess_UnexpectedServerExceptionOccurs_ExceptionIsRethrown() throws Exception {
        whenCallingTheWebservice().thenThrow(aServerErrorException());

        Employee employee = new EmployeeTestBuilder().build();

        taxPaymentWebService.doWebserviceCallToTaxService(employee, Money.of(CurrencyUnit.EUR, 2000.0));
    }

    @Test(expected = TaxWebServiceFatalException.class)
    public void testProcess_ClientExceptionOccurs_TaxWebserviceFatalExceptionIsRethrown() throws Exception {
        whenCallingTheWebservice().thenThrow(aMethodNotAllowedException());

        Employee employee = new EmployeeTestBuilder().build();

        taxPaymentWebService.doWebserviceCallToTaxService(employee, Money.of(CurrencyUnit.EUR, 2000.0));
    }

    private OngoingStubbing<ResponseEntity<TaxServiceResponse>> whenCallingTheWebservice() {
        return when(restTemplateMock.postForEntity(eq(URI.create(HTTP_SOMEHOST_SOMEURL)), any(TaxTo.class), eq(TaxServiceResponse.class)));
    }

    private HttpClientErrorException aMethodNotAllowedException() {
        return new HttpClientErrorException(HttpStatus.METHOD_NOT_ALLOWED);
    }

    private HttpServerErrorException aServerErrorException() {
        return new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResourceAccessException aWrappedTimeOutException() {
        SocketTimeoutException socketTimeoutException = new SocketTimeoutException();
        return new ResourceAccessException("I/O error", socketTimeoutException);
    }
}
