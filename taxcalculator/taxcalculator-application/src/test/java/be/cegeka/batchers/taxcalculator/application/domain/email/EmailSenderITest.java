package be.cegeka.batchers.taxcalculator.application.domain.email;


import be.cegeka.batchers.taxcalculator.application.infrastructure.IntegrationTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static be.cegeka.batchers.taxcalculator.application.ApplicationAssertions.assertThat;

public class EmailSenderITest extends IntegrationTest {

    public static final String TEST_BODY = "test body";
    @Autowired
    private EmailSender emailSender;

    @Before
    public void setUpWiser() {
        SmtpServerStub.start();
    }

    @After
    public void tearDownWiser() {
        SmtpServerStub.stop();
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
}
