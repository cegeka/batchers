package be.cegeka.batchers.taxcalculator.application;

import be.cegeka.batchers.taxcalculator.application.domain.email.SmtpAssertions;
import org.fest.assertions.api.Assertions;
import org.subethamail.wiser.Wiser;

public class ApplicationAssertions  extends Assertions {

    public static SmtpAssertions assertThat(Wiser actual) {
        return new SmtpAssertions(actual);
    }
}