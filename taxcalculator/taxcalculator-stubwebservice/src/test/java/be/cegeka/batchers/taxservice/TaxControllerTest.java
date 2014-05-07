package be.cegeka.batchers.taxservice;

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
    TaxSubmissionLogger taxLogger;
    @Mock
    BlacklistEmployeesService blacklistEmployeesService;
    @InjectMocks
    TaxController taxController = new TaxController();

    @Test
    public void whenAValidTaxToIsReceived_ThenALogLineIsCreated() {
        //Arrange

        //Act
        TaxTo taxTo = new TaxTo("1234567", 123.0);
        taxController.submitTaxForm(taxTo);

        //Assert
        verify(taxLogger, times(1)).log(taxTo, "OK");
    }

    @Test
    public void givenBlacklistEmployee_whenSubmitTaxForm_thenResponseFails() {
        TaxTo taxTo = new TaxTo();
        String employeeId = "1";
        taxTo.setEmployeeId(employeeId);
        when(blacklistEmployeesService.isEmployeeBlacklisted(employeeId)).thenReturn(true);

        ResponseEntity<String> response = taxController.submitTaxForm(taxTo);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(TaxController.RESPONSE_BODY_FAIL);
    }
}
