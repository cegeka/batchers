package be.cegeka.batchers.taxcalculator.application.domain.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.fest.assertions.api.AbstractAssert;
import org.fest.assertions.api.Assertions;

import java.io.IOException;

public class PdfAssertions extends AbstractAssert<PdfAssertions, PDDocument> {

    public PdfAssertions(PDDocument actual) {
        super(actual, PdfAssertions.class);
    }

    public PdfAssertions containsText(String text) {
        Assertions.assertThat(getPdfText()).contains(text);
        return this;
    }

    private String getPdfText() {
        try {
            return new PDFTextStripper().getText(actual);
        } catch (IOException shouldNotHappen) {
            throw new RuntimeException(shouldNotHappen);
        } finally {
            try {
                actual.close();
            } catch (IOException e) {
                // we don't care
            }
        }
    }
}
