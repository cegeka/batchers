package be.cegeka.batchers.taxcalculator.application.domain.pdf;

import fr.opensagres.xdocreport.core.XDocReportException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static be.cegeka.batchers.taxcalculator.application.ApplicationAssertions.assertThat;


public class PDFGeneratorServiceTest {


    @Test
    public void given_aWordTemplate_whenConvertingToPdf_thenTheSamePdfIsGenerated() throws IOException, XDocReportException {
        PDFGeneratorService pdfGeneratorService = new PDFGeneratorService();

        Map<String, Object> context = new HashMap<>();
        context.put("test", "cegeka-batchers");

        byte[] actual = pdfGeneratorService.generatePdfAsByteArray(new ClassPathResource("test_template.docx"), context);

        assertThat(PDDocument.load(new ByteArrayInputStream(actual)))
                .containsText("cegeka-batchers is working");
    }


}
