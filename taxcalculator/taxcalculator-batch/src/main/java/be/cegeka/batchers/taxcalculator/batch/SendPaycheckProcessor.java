package be.cegeka.batchers.taxcalculator.batch;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.domain.email.EmailAttachmentTO;
import be.cegeka.batchers.taxcalculator.application.domain.email.EmailSender;
import be.cegeka.batchers.taxcalculator.application.domain.email.EmailTO;
import be.cegeka.batchers.taxcalculator.application.domain.pdf.PDFGeneratorService;
import org.joda.time.DateTime;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static be.cegeka.batchers.taxcalculator.infrastructure.utils.DateUtils.longMonthAndYearOf;

@Component
public class SendPaycheckProcessor implements ItemProcessor<Employee, Employee> {

    @Value(value = "${paycheck.template:classpath:/paycheck-template.docx}")
    private String paycheckTemplateFileName = "classpath:/paycheck-template.docx";
    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private PDFGeneratorService pdfGeneratorService;
    @Autowired
    private EmailSender emailSender;

    @Value(value = "${paycheck.from.email:finance@email.com}")
    String payCheckFrom;

    @Override
    public Employee process(Employee employee) throws Exception {
        Resource resource = resourceLoader.getResource(paycheckTemplateFileName);

        byte[] pdfBytes = pdfGeneratorService.generatePdfAsByteArray(resource, getPayCheckPdfContext(employee));
        emailSender.send(getEmailTO(employee, pdfBytes));

        return employee;
    }

    public String getEmailBodyForEmployee(Employee employee) {

        String ENDL = "<br/>";
        StringBuilder sb = new StringBuilder()
                .append("Dear employee,")
                .append(ENDL)
                .append("Please find enclosed the paycheck for " + longMonthAndYearOf(employee.getCalculationDate()) + ".")
                .append(ENDL)
                .append("Regards,")
                .append(ENDL)
                .append("The Finance department");
        return sb.toString();
    }

    private Map<String, Object> getPayCheckPdfContext(Employee employee) {
        Map<String, Object> context = new HashMap<>();
        context.put("period", "aa");
        context.put("name", employee.fullName());
        context.put("monthly_income", employee.getIncome());
        context.put("monthly_tax", employee.getIncomeTax());
        context.put("tax_total", employee.getTaxTotal());
        context.put("employee_id", employee.getId());
        return context;
    }

    private EmailTO getEmailTO(Employee employee, byte[] pdfBytes) {
        EmailTO emailTo = new EmailTO();
        emailTo.addTo(employee.getEmail());
        emailTo.setSubject("Paycheck");
        emailTo.setBody(getEmailBodyForEmployee(employee));
        emailTo.setFrom(payCheckFrom);

        EmailAttachmentTO attachmentTO = new EmailAttachmentTO();
        attachmentTO.setBytes(pdfBytes);
        attachmentTO.setName("paycheck.pdf");
        emailTo.addAttachment(attachmentTO);
        return emailTo;
    }

    private String getLongMonthName(DateTime dateTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, dateTime.getMonthOfYear() - 1);

        return calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
    }
}
