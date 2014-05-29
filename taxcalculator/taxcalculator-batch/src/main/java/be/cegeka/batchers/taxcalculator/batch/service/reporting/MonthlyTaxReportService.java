package be.cegeka.batchers.taxcalculator.batch.service.reporting;

import be.cegeka.batchers.taxcalculator.application.domain.MonthlyReport;
import be.cegeka.batchers.taxcalculator.application.domain.MonthlyReportRepository;
import be.cegeka.batchers.taxcalculator.application.domain.pdf.PDFGeneratorService;
import fr.opensagres.xdocreport.core.XDocReportException;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class MonthlyTaxReportService {
    @Autowired
    SumOfTaxes sumOfTaxes;
    @Autowired
    private PDFGeneratorService pdfGeneratorService;
    @Autowired
    private MonthlyReportRepository monthlyReportRepository;

    public byte[] generateReport(long year, long month) throws IOException, XDocReportException {
        return generateReport(year, month, null);
    }

    public byte[] generateReport(long year, long month, Long jobExecutionId) throws IOException, XDocReportException {
        Resource monthlyReportTemplate = new ClassPathResource("monthly-tax-report-template.docx");

        Map<String, Object> contextMap = new HashMap<>();
        contextMap.put("success_sum", sumOfTaxes.getSuccessSum(year, month));
        contextMap.put("failed_sum", sumOfTaxes.getFailedSum(year, month));
        contextMap.put("date", "" + month + " " + year);

        byte[] pdfBytes = pdfGeneratorService.generatePdfAsByteArray(monthlyReportTemplate, contextMap);

        MonthlyReport monthlyReport = MonthlyReport.from(year, month, pdfBytes, DateTime.now(), jobExecutionId);
        monthlyReportRepository.save(monthlyReport);
        return pdfBytes;
    }

    public void setPdfGeneratorService(PDFGeneratorService pdfGeneratorService) {
        this.pdfGeneratorService = pdfGeneratorService;
    }
}
