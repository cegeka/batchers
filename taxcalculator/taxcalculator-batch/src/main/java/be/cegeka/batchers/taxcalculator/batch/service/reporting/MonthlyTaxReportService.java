package be.cegeka.batchers.taxcalculator.batch.service.reporting;

import be.cegeka.batchers.taxcalculator.application.domain.pdf.PDFGeneratorService;
import fr.opensagres.xdocreport.core.XDocReportException;
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

    public byte[] generateReport(int month, int year) throws IOException, XDocReportException {
        Resource monthlyReportTemplate = new ClassPathResource("monthly-tax-report-template.docx");
        Map<String, Object> contextMap = new HashMap<>();
        contextMap.put("success_sum", sumOfTaxes.getSuccessSum());
        contextMap.put("failed_sum", sumOfTaxes.getFailedSum());
        contextMap.put("date", "" + month + " " + year);
        return pdfGeneratorService.generatePdfAsByteArray(monthlyReportTemplate, contextMap);
    }

    public void setPdfGeneratorService(PDFGeneratorService pdfGeneratorService) {
        this.pdfGeneratorService = pdfGeneratorService;
    }
}
