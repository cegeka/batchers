package be.cegeka.batchers.taxcalculator.application;

import be.cegeka.batchers.taxcalculator.application.domain.email.SmtpAssertions;
import be.cegeka.batchers.taxcalculator.application.domain.pdf.PdfAssertions;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.fest.assertions.api.Assertions;
import org.subethamail.wiser.Wiser;

public class ApplicationAssertions  extends Assertions {

    public static SmtpAssertions assertThat(Wiser actual) {
        return new SmtpAssertions(actual);
    }

    public static PdfAssertions assertThat(PDDocument actual) {
        return new PdfAssertions(actual);
    }
}