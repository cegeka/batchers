package be.cegeka.batchers.taxcalculator.batch.processor;

import be.cegeka.batchers.taxcalculator.application.domain.*;
import be.cegeka.batchers.taxcalculator.application.domain.email.EmailAttachmentTO;
import be.cegeka.batchers.taxcalculator.application.domain.email.EmailSender;
import be.cegeka.batchers.taxcalculator.application.domain.email.EmailTO;
import be.cegeka.batchers.taxcalculator.application.domain.pdf.PDFGeneratorService;
import be.cegeka.batchers.taxcalculator.batch.domain.*;
import be.cegeka.batchers.taxcalculator.batch.repositories.TaxCalculationRepository;
import fr.opensagres.xdocreport.core.XDocReportException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.StepExecution;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.Map;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SendPaycheckProcessorTest {

    public static final String EMPLOYEE_EMAIL = "employee1123@work.com";

    @InjectMocks
    private SendPaycheckProcessor sendPaycheckProcessor;

    @Mock
    private EmailSender emailSender;
    @Mock
    private PDFGeneratorService pdfGeneratorService;
    @Captor
    private ArgumentCaptor<Map<String, Object>> contextCaptor;
    @Captor
    private ArgumentCaptor<EmailTO> emailToCaptor;
    @Mock
    private ResourceLoader resourceLoader;
    @Mock
    private TaxCalculationRepository taxCalculationRepository;

    @Mock
    private StepExecution stepExecution;
    private Long jobExecutionId;
    private Employee employee;
    private TaxCalculation taxCalculation;
    private TaxWebserviceCallResult taxWebserviceCallResult;
    private byte[] generatedPdfBytes;


    @Before
    public void setUp() throws IOException, XDocReportException {
        jobExecutionId = 1L;

        employee = new EmployeeTestBuilder()
                .withEmailAddress(EMPLOYEE_EMAIL)
                .withFirstName("FirstName")
                .withIncome(2000)
                .build();

        taxCalculation = new TaxCalculationTestBuilder().withEmployee(employee).withTax(10.0).build();
        taxWebserviceCallResult = TaxWebserviceCallResult.callSucceeded(taxCalculation);

        generatedPdfBytes = new byte[]{0, 1, 2, 3, 4};
        when(pdfGeneratorService.generatePdfAsByteArray(any(), anyMap())).thenReturn(generatedPdfBytes);

        when(resourceLoader.getResource("classpath:/paycheck-template.docx")).thenReturn(new ClassPathResource("paycheck-template.docx"));
        when(stepExecution.getJobExecutionId()).thenReturn(jobExecutionId);
    }

    @Test
    public void givenAnEmployee_whenProcessEmployee_thenAnEmailWithTheGeneratedPDFIsSent() throws Exception {
        PayCheck payCheck = sendPaycheckProcessor.process(taxWebserviceCallResult);

        assertThat(payCheck.getTaxCalculation().getEmployee()).isEqualTo(employee);

        ArgumentCaptor<Resource> taxSummaryTemplateCaptor = ArgumentCaptor.forClass(Resource.class);
        verify(pdfGeneratorService).generatePdfAsByteArray(taxSummaryTemplateCaptor.capture(), contextCaptor.capture());
        assertThat(taxSummaryTemplateCaptor.getValue()).isNotNull();

        Map<String, Object> context = contextCaptor.getValue();
        assertThat(context).containsKey("period")
                .containsKey("name")
                .containsKey("name")
                .containsKey("employee_id")
                .containsKey("monthly_income")
                .containsKey("monthly_tax");

        verify(emailSender).send(emailToCaptor.capture());
        EmailTO capturedEmailTo = emailToCaptor.getValue();

        assertThat(capturedEmailTo.getTos()).containsOnly(employee.getEmail());
        assertThat(capturedEmailTo.getSubject()).isEqualTo("Paycheck");
        String emailBodyForEmployee = sendPaycheckProcessor.getEmailBodyForEmployee(taxCalculation);
        assertThat(capturedEmailTo.getBody()).isEqualTo(emailBodyForEmployee);


        EmailAttachmentTO attachmentTO = capturedEmailTo.getAttachments().get(0);
        assertThat(attachmentTO.getBytes()).contains(generatedPdfBytes);
    }

    @Test
    public void givenAnEmployee_whenProcessEmployee_thenJobExecutionIdIsSetOnPaycheck() throws Exception {

        PayCheck payCheck = sendPaycheckProcessor.process(taxWebserviceCallResult);

        assertThat(payCheck.getJobExecutionId()).isEqualTo(jobExecutionId);
    }
}
