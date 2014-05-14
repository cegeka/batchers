package be.cegeka.batchers.taxcalculator.batch;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.domain.email.EmailAttachmentTO;
import be.cegeka.batchers.taxcalculator.application.domain.email.EmailSender;
import be.cegeka.batchers.taxcalculator.application.domain.email.EmailTO;
import be.cegeka.batchers.taxcalculator.application.domain.pdf.PDFGeneratorService;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.util.Map;

import static be.cegeka.batchers.taxcalculator.batch.SendPaycheckProcessor.PAYCHECK_TEMPLATE_FILE_NAME;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SendPaycheckProcessorTest {
    public static final String EMPLOYEE_EMAIL = "employee1123@work.com";
    @Mock
    EmailSender emailSender;
    @Mock
    PDFGeneratorService pdfGeneratorService;
    @InjectMocks
    SendPaycheckProcessor sendPaycheckProcessor;
    @Captor
    ArgumentCaptor<Map<String, Object>> contextCaptor;
    @Captor
    ArgumentCaptor<EmailTO> emailToCaptor;

    @Test
    public void givenAnEmployee_whenProcessEmployee_thenAnEmailWithTheGeneratedPDFIsSent() throws Exception {
        Employee employee = new Employee();
        employee.setEmail(EMPLOYEE_EMAIL);
        employee.setFirstName("FirstName");
        employee.setIncome(2000);
        DateTime calculationDate = new DateTime().withDate(2000,5,1);

        employee.setCalculationDate(calculationDate);

        byte[] generatedPdfBytes = new byte[]{0, 1, 2, 3, 4};
        when(pdfGeneratorService.generatePdfAsByteArray(any(), anyMap())).thenReturn(generatedPdfBytes);

        Employee processedEmployee = sendPaycheckProcessor.process(employee);

        assertThat(processedEmployee).isEqualTo(employee);

        ArgumentCaptor<File> taxSummaryTemplateCaptor = ArgumentCaptor.forClass(File.class);
        verify(pdfGeneratorService).generatePdfAsByteArray(taxSummaryTemplateCaptor.capture(), contextCaptor.capture());
        assertThat(taxSummaryTemplateCaptor.getValue().getName()).endsWith(PAYCHECK_TEMPLATE_FILE_NAME);

        Map<String, Object> context = contextCaptor.getValue();
        assertThat(context).containsKey("period").
                containsKey("name").
                containsKey("name").
                containsKey("employee_id").
                containsKey("monthly_income").
                containsKey("monthly_tax").
                containsKey("tax_total");

        verify(emailSender).send(emailToCaptor.capture());
        EmailTO capturedEmailTo = emailToCaptor.getValue();

        assertThat(capturedEmailTo.getTos()).containsOnly(employee.getEmail());
        assertThat(capturedEmailTo.getSubject()).isEqualTo("Paycheck");
        String emailBodyForEmployee = sendPaycheckProcessor.getEmailBodyForEmployee(employee);
        assertThat(emailBodyForEmployee).contains("May 2000");
        assertThat(capturedEmailTo.getBody()).isEqualTo(emailBodyForEmployee);


        EmailAttachmentTO attachmentTO = capturedEmailTo.getAttachments().get(0);
        assertThat(attachmentTO.getBytes()).contains(generatedPdfBytes);
    }
}
