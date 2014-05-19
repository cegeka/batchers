package be.cegeka.batchers.taxcalculator.batch.service.reporting;

import be.cegeka.batchers.taxcalculator.application.domain.pdf.PDFGeneratorService;
import fr.opensagres.xdocreport.core.XDocReportException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static be.cegeka.batchers.taxcalculator.application.ApplicationAssertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class MonthlyTaxReportServiceTest {

    public static final double FAILED_AMOUNT = 450.1D;
    public static final double SUCCESS_AMOUNT = 600.1D;
    @Mock
    SumOfTaxes sumOfTaxes;

    @InjectMocks
    MonthlyTaxReportService monthlyTaxReportService;

    @Before
    public void setUp() throws Exception {
        PDFGeneratorService pdfGeneratorService = new PDFGeneratorService();
        monthlyTaxReportService.setPdfGeneratorService(pdfGeneratorService);
    }

    @Test
    public void generateReportWithCorrectData() throws IOException, XDocReportException {
        Mockito.when(sumOfTaxes.getFailedSum()).thenReturn(FAILED_AMOUNT);
        Mockito.when(sumOfTaxes.getSuccessSum()).thenReturn(SUCCESS_AMOUNT);

        byte[] pdfBytes = monthlyTaxReportService.generateReport();

        PDDocument pdfDocument = PDDocument.load(new ByteArrayInputStream(pdfBytes));
        assertThat(pdfDocument)
                .containsText("WEBSERVICE RETURNS SUCCESS " + SUCCESS_AMOUNT + " euro");


        pdfDocument = PDDocument.load(new ByteArrayInputStream(pdfBytes));
        assertThat(pdfDocument)
                .containsText("WEBSERVICE RETURNS FAILURE " + FAILED_AMOUNT + " euro");
    }
}
