package be.cegeka.batchers.taxcalculator.batch;

import be.cegeka.batchers.taxcalculator.application.domain.*;
import be.cegeka.batchers.taxcalculator.application.domain.email.EmailAttachmentTO;
import be.cegeka.batchers.taxcalculator.application.domain.email.EmailSender;
import be.cegeka.batchers.taxcalculator.application.domain.email.EmailTO;
import be.cegeka.batchers.taxcalculator.application.domain.pdf.PDFGeneratorService;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;

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
    SendPaycheckProcessor sendPaycheckProcessor;

    @Mock
    EmailSender emailSender;
    @Mock
    PDFGeneratorService pdfGeneratorService;
    @Mock
    private ResourceLoader resourceLoader;
    @Captor
    ArgumentCaptor<Map<String, Object>> contextCaptor;
    @Captor
    ArgumentCaptor<EmailTO> emailToCaptor;
    @Mock
    private TaxCalculationRepository taxCalculationRepository;


    @Before
    public void setUp() {
        when(resourceLoader.getResource("classpath:/paycheck-template.docx")).thenReturn(new ClassPathResource("paycheck-template.docx"));
    }

    @Test
    public void givenAnEmployee_whenProcessEmployee_thenAnEmailWithTheGeneratedPDFIsSent() throws Exception {
        Employee employee = new EmployeeBuilder()
                .withEmailAddress(EMPLOYEE_EMAIL)
                .withFirstName("FirstName")
                .withIncome(2000)
                .build();

        byte[] generatedPdfBytes = new byte[]{0, 1, 2, 3, 4};
        when(pdfGeneratorService.generatePdfAsByteArray(any(), anyMap())).thenReturn(generatedPdfBytes);

        TaxCalculation taxCalculation = TaxCalculation.from(1L, employee, 2014, 1, Money.of(CurrencyUnit.EUR, 10.0));

        TaxServiceCallResult taxServiceCallResult = TaxServiceCallResult.from(taxCalculation, "", HttpStatus.OK.value(), "", DateTime.now(), true);

        PayCheck payCheck = sendPaycheckProcessor.process(taxServiceCallResult);

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
//                .containsKey("tax_total");
        // TODO IN STEP 3

        verify(emailSender).send(emailToCaptor.capture());
        EmailTO capturedEmailTo = emailToCaptor.getValue();

        assertThat(capturedEmailTo.getTos()).containsOnly(employee.getEmail());
        assertThat(capturedEmailTo.getSubject()).isEqualTo("Paycheck");
        String emailBodyForEmployee = sendPaycheckProcessor.getEmailBodyForEmployee(employee);
//        assertThat(emailBodyForEmployee).contains("May 2000");
        // TODO IN STEP 3
        assertThat(capturedEmailTo.getBody()).isEqualTo(emailBodyForEmployee);


        EmailAttachmentTO attachmentTO = capturedEmailTo.getAttachments().get(0);
        assertThat(attachmentTO.getBytes()).contains(generatedPdfBytes);
    }
}
