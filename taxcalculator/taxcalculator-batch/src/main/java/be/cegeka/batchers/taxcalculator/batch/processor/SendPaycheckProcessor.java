package be.cegeka.batchers.taxcalculator.batch.processor;

import be.cegeka.batchers.taxcalculator.application.domain.PayCheck;
import be.cegeka.batchers.taxcalculator.application.domain.TaxCalculation;
import be.cegeka.batchers.taxcalculator.application.domain.TaxServiceCallResult;
import be.cegeka.batchers.taxcalculator.application.domain.email.EmailAttachmentTO;
import be.cegeka.batchers.taxcalculator.application.domain.email.EmailSender;
import be.cegeka.batchers.taxcalculator.application.domain.email.EmailTO;
import be.cegeka.batchers.taxcalculator.application.domain.pdf.PDFGeneratorService;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@StepScope
public class SendPaycheckProcessor implements ItemProcessor<TaxServiceCallResult, PayCheck> {

    @Value(value = "${paycheck.from.email:finance@email.com}")
    String payCheckFrom;
    @Value(value = "${paycheck.template:classpath:/paycheck-template.docx}")
    private String paycheckTemplateFileName = "classpath:/paycheck-template.docx";
    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private PDFGeneratorService pdfGeneratorService;
    @Autowired
    private EmailSender emailSender;

    @Value("#{stepExecution}")
    private StepExecution stepExecution;

    @Override
    public PayCheck process(TaxServiceCallResult taxServiceCallResult) throws Exception {
        Resource resource = resourceLoader.getResource(paycheckTemplateFileName);

        TaxCalculation taxCalculation = taxServiceCallResult.getTaxCalculation();
        byte[] pdfBytes = pdfGeneratorService.generatePdfAsByteArray(resource, getPayCheckPdfContext(taxCalculation));
        emailSender.send(getEmailTO(taxCalculation, pdfBytes));

        return PayCheck.from(stepExecution.getJobExecutionId(), taxCalculation, pdfBytes);
    }

    public String getEmailBodyForEmployee(TaxCalculation taxCalculation) {

        String newline = "<br/>";
        StringBuilder sb = new StringBuilder()
                .append("Dear employee,")
                .append(newline)
                .append("Please find enclosed the paycheck for ")
                .append(getYearMonth(taxCalculation))
                .append(newline)
                .append("Regards,")
                .append(newline)
                .append("The Finance department");
        return sb.toString();
    }

    private Map<String, Object> getPayCheckPdfContext(TaxCalculation taxCalculation) {
        Map<String, Object> context = new HashMap<>();
        context.put("period", getYearMonth(taxCalculation));
        context.put("name", taxCalculation.getEmployee().fullName());
        context.put("monthly_income", taxCalculation.getEmployee().getIncome());
        context.put("monthly_tax", taxCalculation.getTax().getAmount());
        context.put("employee_id", taxCalculation.getEmployee().getId());
        return context;
    }

    private String getYearMonth(TaxCalculation taxCalculation) {
        return taxCalculation.getYear() + " " + taxCalculation.getMonth();
    }

    private EmailTO getEmailTO(TaxCalculation taxCalculation, byte[] pdfBytes) {
        EmailTO emailTo = new EmailTO();
        emailTo.addTo(taxCalculation.getEmployee().getEmail());
        emailTo.setSubject("Paycheck");
        emailTo.setBody(getEmailBodyForEmployee(taxCalculation));
        emailTo.setFrom(payCheckFrom);

        EmailAttachmentTO attachmentTO = new EmailAttachmentTO();
        attachmentTO.setBytes(pdfBytes);
        attachmentTO.setName("paycheck.pdf");
        emailTo.addAttachment(attachmentTO);
        return emailTo;
    }

}
