package be.cegeka.batchers.taxcalculator.application.domain.email;


import be.cegeka.batchers.taxcalculator.application.infrastructure.IntegrationTest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static be.cegeka.batchers.taxcalculator.application.ApplicationAssertions.assertThat;

public class EmailSenderITest extends IntegrationTest {

    private static final String TEST_BODY = "test body";

    @Autowired
    private EmailSender emailSender;

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
        emailSender.jobHasBeenStarted("a string");
    }

    @Test
    public void sendAnEmailToMe() {
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

        emailSender.send(emailTO);

        assertThat(SmtpServerStub.wiser())
                .hasReceivedMessages(1)
                .hasReceivedMessageContaining(TEST_BODY);
    }

    @Test
    public void onlyOneEmailIsSentAtAllTime() {
        EmailTO emailTO = new EmailTO();

        emailTO.addTo("radu.cirstoiu@cegeka.com");
        emailTO.setFrom("seagulls.cgk@gmail.com");
        emailTO.setSubject("test subject");
        emailTO.setBody(TEST_BODY);

        emailSender.send(emailTO);
        emailSender.send(emailTO);

        assertThat(SmtpServerStub.wiser())
                .hasReceivedMessages(1)
                .hasReceivedMessageContaining(TEST_BODY);
    }
}
