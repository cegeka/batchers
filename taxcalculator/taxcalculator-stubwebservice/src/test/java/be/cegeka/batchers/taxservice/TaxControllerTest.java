package be.cegeka.batchers.taxservice;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TaxControllerTest {

    @Mock
    private TextFileTaxLogger taxLogger;

    @Mock
    private SpecialEmployeesService specialEmployeesService;

    @InjectMocks
    private TaxController taxController;

    private TaxTo taxTo;
    private String employeeId;

    @Before
    public void setup() {
        employeeId = "1";
        taxTo = new TaxTo(employeeId, 123.0);
    }

    @Test
    public void givenValidTaxTo_whenSubmitTaxForm_ThenALogLineIsCreated() {
        taxController.submitTaxForm(taxTo);

        verify(taxLogger, times(1)).log(taxTo, "OK");
    }

    @Test
    public void givenBlacklistEmployee_whenSubmitTaxForm_thenResponseFails() {
        when(specialEmployeesService.isEmployeeBlacklisted(employeeId)).thenReturn(true);

        ResponseEntity<String> response = taxController.submitTaxForm(taxTo);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(TaxController.RESPONSE_BODY_FAIL);
    }

    @Test
    public void givenEmployeeWithTax_whenSubmitTaxForm_thenItSleepsIfNecessary() throws InterruptedException {

        taxController.submitTaxForm(taxTo);

        verify(specialEmployeesService).sleepIfNecessary(employeeId);
    }

    @Test
    public void givenBlacklistEmployee_whenSubmitTaxForm_thenDoNotTryToTimeout() {
        when(specialEmployeesService.isEmployeeBlacklisted(employeeId)).thenReturn(true);

        taxController.submitTaxForm(taxTo);

        verify(specialEmployeesService, times(0)).sleepIfNecessary(employeeId);
    }
}
