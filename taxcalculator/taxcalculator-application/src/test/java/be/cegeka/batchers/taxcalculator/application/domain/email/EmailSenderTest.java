package be.cegeka.batchers.taxcalculator.application.domain.email;


import org.apache.commons.mail.Email;
import org.junit.Test;

import javax.mail.MessagingException;
import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

public class EmailSenderTest {

    @Test
    public void givenAnEmailTO_whenSendingAnEmail_thenTheCorrectDataIsUsedAndTheEmailIsSent() throws Exception {
        EmailSender.EmailMapper emailMapper = new EmailSender.EmailMapper();

        Email email = emailMapper.mapFromEmailTO(emailTO());

        assertionsForEmail(email, emailTO());
    }

    private void assertionsForEmail(Email actual, EmailTO expected) throws IOException, MessagingException {
        assertThat(actual.getFromAddress().getAddress()).isEqualTo(expected.getFrom());
        assertThat(actual.getToAddresses().iterator().next().getAddress()).isEqualTo(expected.getTos().iterator().next());
        assertThat(actual.getSubject()).isEqualTo(expected.getSubject());
    }

    private EmailTO emailTO() {
        EmailTO to = new EmailTO();
        to.setFrom("createNewDossierFrom@domain");
        to.addTo("client@otherdomain");
        to.setSubject("subject");
        to.setBody("body");
        return to;
    }
}