package be.cegeka.batchers.taxcalculator.batch.service.reporting;

import be.cegeka.batchers.taxcalculator.application.domain.pdf.PDFGeneratorService;
import be.cegeka.batchers.taxcalculator.application.domain.reporting.MonthlyReport;
import be.cegeka.batchers.taxcalculator.application.domain.reporting.MonthlyReportRepository;
import fr.opensagres.xdocreport.core.XDocReportException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static be.cegeka.batchers.taxcalculator.application.ApplicationAssertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MonthlyTaxReportServiceTest {
    public static final double FAILED_AMOUNT = 450.1D;
    public static final double SUCCESS_AMOUNT = 600.1D;
    public static final int TEST_YEAR = 2014;
    public static final int TEST_MONTH = 5;

    @InjectMocks
    MonthlyTaxReportService monthlyTaxReportService;

    @Mock
    SumOfTaxes sumOfTaxes;
    @Mock
    MonthlyReportRepository monthlyReportRepository;

    @Captor
    private ArgumentCaptor<MonthlyReport> monthlyReportArgumentCaptor;

    @Before
    public void setUp() throws Exception {
        PDFGeneratorService pdfGeneratorService = new PDFGeneratorService();
        monthlyTaxReportService.setPdfGeneratorService(pdfGeneratorService);
        when(sumOfTaxes.getFailedSum(TEST_YEAR, TEST_MONTH)).thenReturn(FAILED_AMOUNT);
        when(sumOfTaxes.getSuccessSum(TEST_YEAR, TEST_MONTH)).thenReturn(SUCCESS_AMOUNT);
    }

    @Test
    public void generateReportWithCorrectData() throws IOException, XDocReportException {
        byte[] pdfBytes = monthlyTaxReportService.generateReport(3L, TEST_YEAR, TEST_MONTH);

        PDDocument pdfDocument = PDDocument.load(new ByteArrayInputStream(pdfBytes));
        assertThat(pdfDocument)
                .containsText("WEBSERVICE RETURNS SUCCESS " + SUCCESS_AMOUNT + " euro");


        pdfDocument = PDDocument.load(new ByteArrayInputStream(pdfBytes));
        assertThat(pdfDocument)
                .containsText("WEBSERVICE RETURNS FAILURE " + FAILED_AMOUNT + " euro");

        pdfDocument = PDDocument.load(new ByteArrayInputStream(pdfBytes));
        assertThat(pdfDocument)
                .containsText("PERIOD: " + 5 + " " + TEST_YEAR);
    }

    @Test
    public void testResultsArePersisted() throws Exception {
        byte[] expectedPdfBytes = monthlyTaxReportService.generateReport(3L, TEST_YEAR, TEST_MONTH);

        verify(monthlyReportRepository).save(monthlyReportArgumentCaptor.capture());

        MonthlyReport value = monthlyReportArgumentCaptor.getValue();
        assertThat(value.getMonthlyReportPdf()).isEqualTo(expectedPdfBytes);
        assertThat(value.getYear()).isEqualTo(TEST_YEAR);
        assertThat(value.getMonth()).isEqualTo(TEST_MONTH);
        assertThat(value.getCalculationDate()).isNotNull();
    }
}
