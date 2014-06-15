package be.cegeka.batchers.taxcalculator.application.service;


import be.cegeka.batchers.taxcalculator.application.domain.email.EmailAttachmentTO;
import be.cegeka.batchers.taxcalculator.application.domain.email.EmailTO;
import be.cegeka.batchers.taxcalculator.application.domain.email.SmtpServerStub;
import be.cegeka.batchers.taxcalculator.application.infrastructure.IntegrationTest;
import be.cegeka.batchers.taxcalculator.application.service.EmailSenderService;
import be.cegeka.batchers.taxcalculator.application.service.exceptions.EmailSenderException;
import org.apache.commons.mail.EmailException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static be.cegeka.batchers.taxcalculator.application.ApplicationAssertions.assertThat;

public class EmailSenderServiceITest extends IntegrationTest {

    private static final String TEST_BODY = "test body";

    @Autowired
    private EmailSenderService emailSenderService;

    @BeforeClass
    public static void setUpWiser() {
        SmtpServerStub.start();
    }

    @AfterClass
    public static void tearDownWiser() {
        SmtpServerStub.stop();
    }

    @After
    public void resetSendMessages() {
        SmtpServerStub.clearMessages();
        emailSenderService.jobHasBeenStarted("a string");
    }

    @Test
    public void sendAnEmailToMe() throws EmailSenderException {
        EmailTO emailTO = new EmailTO();

        EmailAttachmentTO attachmentTO1 = new EmailAttachmentTO();
        attachmentTO1.setBytes(new byte[256]);
        attachmentTO1.setName("test.pdf");

        EmailAttachmentTO attachmentTO2 = new EmailAttachmentTO();
        attachmentTO2.setBytes(new byte[256]);
        attachmentTO2.setName("test2.pdf");


        emailTO.addTo("radu.cirstoiu@cegeka.com");
        emailTO.setFrom("seagulls.cgk@gmail.com");
        emailTO.setSubject("test subject");
        emailTO.setBody(TEST_BODY);
        emailTO.addAttachment(attachmentTO1);
        emailTO.addAttachment(attachmentTO2);

        emailSenderService.send(emailTO);

        assertThat(SmtpServerStub.wiser())
                .hasReceivedMessages(1)
                .hasReceivedMessageContaining(TEST_BODY);
    }

    @Test
    public void onlyOneEmailIsSentAtAllTime() throws EmailSenderException {
        EmailTO emailTO = new EmailTO();

        emailTO.addTo("radu.cirstoiu@cegeka.com");
        emailTO.setFrom("seagulls.cgk@gmail.com");
        emailTO.setSubject("test subject");
        emailTO.setBody(TEST_BODY);

        emailSenderService.send(emailTO);
        emailSenderService.send(emailTO);

        assertThat(SmtpServerStub.wiser())
                .hasReceivedMessages(1)
                .hasReceivedMessageContaining(TEST_BODY);
    }
}
