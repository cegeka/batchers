package be.cegeka.batchers.taxcalculator.batch;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.domain.EmployeeBuilder;
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
import static org.mockito.Mockito.when;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

@RunWith(MockitoJUnitRunner.class)
public class CallWebserviceProcessorTest {

    public static final String HTTP_SOMEHOST_SOMEURL = "http://somehost/someurl";
    @InjectMocks
    private CallWebserviceProcessor callWebserviceProcessor;

    @Mock
    private RestTemplate restTemplateMock;

    @Mock
    private ResponseEntity<String> mockedResponse;

    @Before
    public void setUpCallWebserviceProcessor() {
        setInternalState(callWebserviceProcessor, "taxServiceUrl", HTTP_SOMEHOST_SOMEURL);
    }

    @Test
    public void testProcessHappyPath_NoExceptionHasBeenThrownAndEmployeeIsReturned() throws Exception {
        when(restTemplateMock.postForEntity(eq(URI.create(HTTP_SOMEHOST_SOMEURL)), any(TaxTo.class), eq(String.class))).thenReturn(mockedResponse);
        when(mockedResponse.getBody()).thenReturn("OK");

        Employee employee = new EmployeeBuilder().build();

        assertThat(callWebserviceProcessor.process(employee)).isEqualTo(employee);
    }

    @Test(expected = RuntimeException.class)
    public void testProcessBadResponse_ExceptionHasBeenThrownAndEmployeeIsReturned() throws Exception {
        when(restTemplateMock.postForEntity(eq(URI.create(HTTP_SOMEHOST_SOMEURL)), any(TaxTo.class), eq(String.class))).thenReturn(mockedResponse);
        when(mockedResponse.getBody()).thenReturn("ERROR");

        Employee employee = new EmployeeBuilder().build();

        callWebserviceProcessor.process(employee);
    }
}
