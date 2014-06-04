package be.cegeka.batchers.taxcalculator.batch.service.reporting;

import be.cegeka.batchers.taxcalculator.application.domain.reporting.MonthlyReport;
import be.cegeka.batchers.taxcalculator.application.repositories.MonthlyReportRepository;
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

    public byte[] generateReport(Long jobExecutionId, int year, int month) throws IOException, XDocReportException {
        Resource monthlyReportTemplate = new ClassPathResource("monthly-tax-report-template.docx");

        Map<String, Object> contextMap = new HashMap<>();
        contextMap.put("success_sum", sumOfTaxes.getSuccessSum(year, month));
        contextMap.put("failed_sum", sumOfTaxes.getFailedSum(year, month));
        contextMap.put("date", month + " " + year);

        byte[] pdfBytes = pdfGeneratorService.generatePdfAsByteArray(monthlyReportTemplate, contextMap);

        MonthlyReport monthlyReport = MonthlyReport.from(jobExecutionId, year, month, pdfBytes, DateTime.now());
        monthlyReportRepository.save(monthlyReport);
        return pdfBytes;
    }

    public void setPdfGeneratorService(PDFGeneratorService pdfGeneratorService) {
        this.pdfGeneratorService = pdfGeneratorService;
    }
}
