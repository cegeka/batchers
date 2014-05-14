package be.cegeka.batchers.taxcalculator.batch;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.domain.email.EmailAttachmentTO;
import be.cegeka.batchers.taxcalculator.application.domain.email.EmailSender;
import be.cegeka.batchers.taxcalculator.application.domain.email.EmailTO;
import be.cegeka.batchers.taxcalculator.application.domain.pdf.PDFGeneratorService;
import org.joda.time.DateTime;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Component
public class SendPaycheckProcessor implements ItemProcessor<Employee, Employee> {

    public static final String PAYCHECK_TEMPLATE_FILE_NAME = "paycheck-template.docx";
    @Autowired
    PDFGeneratorService pdfGeneratorService;
    @Autowired
    EmailSender emailSender;

    @Override
    public Employee process(Employee employee) throws Exception {
        URL resource = getClass().getClassLoader().getResource(PAYCHECK_TEMPLATE_FILE_NAME);

        File paycheckTemplateFile = new File(resource.toURI());

        byte[] pdfBytes = pdfGeneratorService.generatePdfAsByteArray(paycheckTemplateFile, getPayCheckPdfContext(employee));
        emailSender.send(getEmailTO(employee, pdfBytes));

        return employee;
    }

    public String getEmailBodyForEmployee(Employee employee) {
        StringBuilder sb = new StringBuilder();

        sb
                .append("Dear employee,")
                .append("\n\n")
                .append("Please find enclosed the paycheck for " + getLongMonthName(employee.getCalculationDate()) + " " + employee.getCalculationDate().getYear() + ".")
                .append("\n\n")
                .append("Regards,")
                .append("The Finance department")
        ;
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
        emailTo.setFrom("finance@email.com");

        EmailAttachmentTO attachmentTO = new EmailAttachmentTO();
        attachmentTO.setBytes(pdfBytes);
        attachmentTO.setName("paycheck.pdf");
        emailTo.addAttachment(attachmentTO);
        return emailTo;
    }

    private String getLongMonthName(DateTime dateTime){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, dateTime.getMonthOfYear() -1);

        return calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
    }
}
