package be.cegeka.batchers.taxcalculator.application.service;


import be.cegeka.batchers.taxcalculator.application.domain.email.EmailTO;
import be.cegeka.batchers.taxcalculator.application.service.exceptions.EmailSenderException;
import org.apache.commons.mail.Email;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

@RunWith(MockitoJUnitRunner.class)
public class EmailSenderServiceTest {

    @Rule
    public ExpectedException exception = none();

    private EmailSenderService emailSenderService;

    @Before
    public void setUp() throws Exception {
        emailSenderService = new EmailSenderService();
    }

    @Test
    public void givenAnEmailTO_whenSendingAnEmail_thenTheCorrectDataIsUsedAndTheEmailIsSent() throws Exception {
        EmailSenderService.EmailMapper emailMapper = new EmailSenderService.EmailMapper();

        Email email = emailMapper.mapFromEmailTO(emailTO());

        assertionsForEmail(email, emailTO());
    }

    @Test
    public void whenSMTP_port_CredentialsIsNotConfigured_anExceptionIsThrown() throws Exception {
        exception.expect(EmailSenderException.class);
        exception.expectMessage("SMTP port is not configured");

        emailSenderService.send(emailTO());
    }

    @Test
    public void whenSMTP_server_IsNotConfigured_anExceptionIsThrown() throws Exception {
        exception.expect(EmailSenderException.class);
        exception.expectMessage("SMTP server is not configured");

        setInternalState(emailSenderService, "smtp_port", "2500");
        emailSenderService.send(emailTO());
    }

    @Test
    public void whenSMTP_username_IsNotConfigured_anExceptionIsThrown() throws Exception {
        exception.expect(EmailSenderException.class);
        exception.expectMessage("SMTP username is not configured");

        setInternalState(emailSenderService, "smtp_port", "2500");
        setInternalState(emailSenderService, "smtp_server", "smtp.server.com");

        emailSenderService.send(emailTO());
    }

    @Test
    public void whenSMTP_password_IsNotConfigured_anExceptionIsThrown() throws Exception {
        exception.expect(EmailSenderException.class);
        exception.expectMessage("SMTP password is not configured");

        setInternalState(emailSenderService, "smtp_port", "2500");
        setInternalState(emailSenderService, "smtp_server", "smtp.server.com");
        setInternalState(emailSenderService, "smtp_username", "username");

        emailSenderService.send(emailTO());
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