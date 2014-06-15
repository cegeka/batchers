package be.cegeka.batchers.taxcalculator.application.service;


import be.cegeka.batchers.taxcalculator.application.domain.email.EmailTO;
import be.cegeka.batchers.taxcalculator.application.service.EmailSenderService;
import org.apache.commons.mail.Email;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class EmailSenderServiceTest {

    @Test
    public void givenAnEmailTO_whenSendingAnEmail_thenTheCorrectDataIsUsedAndTheEmailIsSent() throws Exception {
        EmailSenderService.EmailMapper emailMapper = new EmailSenderService.EmailMapper();

        Email email = emailMapper.mapFromEmailTO(emailTO());

        assertionsForEmail(email, emailTO());
    }

    private void assertionsForEmail(Email actual, EmailTO expected) {
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