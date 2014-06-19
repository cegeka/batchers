package be.cegeka.batchers.taxcalculator.application.service;

import be.cegeka.batchers.taxcalculator.application.domain.email.EmailAttachmentTO;
import be.cegeka.batchers.taxcalculator.application.domain.email.EmailTO;
import be.cegeka.batchers.taxcalculator.application.service.exceptions.EmailSenderException;
import be.cegeka.batchers.taxcalculator.batch.api.JobStartListener;
import org.apache.commons.mail.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.*;

@Service
public class EmailSenderService implements JobStartListener {
    private static final Logger LOG = LoggerFactory.getLogger(EmailSenderService.class);

    private static final int MAX_EMAILS_TO_BE_SEND = 5;

    @Value("${smtp_use_ssl:true}")
    private boolean smtpUseSsl;

    @Autowired
    private String smtp_port;

    @Autowired
    private String smtp_server;

    @Autowired
    private String smtp_username;

    @Autowired
    private String smtp_password;

    private int emailSendCounter;

    public void send(EmailTO emailTO) throws EmailSenderException {
        try {
            checkIfEmailCredentialsAreConfigured();
            if (isNotBlank(smtp_server) && emailSendCounter < MAX_EMAILS_TO_BE_SEND) {
                Email email = new EmailMapper().mapFromEmailTO(emailTO);
                email.setSSLOnConnect(smtpUseSsl);
                email.setSmtpPort(Integer.valueOf(smtp_port));
                email.setHostName(smtp_server);
                if (isNoneBlank(smtp_username, smtp_password)) {
                    email.setAuthenticator(new DefaultAuthenticator(smtp_username, smtp_password));
                }

                LOG.info("Sending email: " + emailTO);
                email.send();
                emailSendCounter++;
            }
        } catch (IllegalArgumentException e) {
            LOG.error("IllegalArgumentException occurred while sending the email ", e);
            throw e;
        } catch (EmailException | IOException e) {
            LOG.error("Errors occurred while sending the email ", e);
            throw new EmailSenderException(e);
        }
    }

    private void checkIfEmailCredentialsAreConfigured() throws EmailSenderException {
        if (isBlank(smtp_port)) {
            throw new EmailSenderException("SMTP port is not configured");
        }

        if (isBlank(smtp_server)) {
            throw new EmailSenderException("SMTP server is not configured");
        }

        if (isBlank(smtp_username)) {
            throw new EmailSenderException("SMTP username is not configured");
        }

        if (isBlank(smtp_password)) {
            throw new EmailSenderException("SMTP password is not configured");
        }

    }

    @Override
    public void jobHasBeenStarted(String jobName) {
        emailSendCounter = 0;
    }

    static class EmailMapper {
        private static InternetAddress toInternetAddress(String address) {
            try {
                return new InternetAddress(address);
            } catch (AddressException e) {
                throw new IllegalArgumentException(e);
            }
        }

        public Email mapFromEmailTO(EmailTO emailTO) throws EmailException, IOException {
            HtmlEmail email = new HtmlEmail();
            email.setFrom(emailTO.getFrom());
            email.setTo(convertToInternetAddress(emailTO.getTos()));
            email.setSubject(emailTO.getSubject());
            email.setHtmlMsg(emailTO.getBody());
            attachEmailAttachmentTOs(email, emailTO.getAttachments());
            return email;
        }

        private Set<InternetAddress> convertToInternetAddress(List<String> emailAddresses) {
            return emailAddresses.stream().map(EmailMapper::toInternetAddress).collect(toSet());
        }

        private void attachEmailAttachmentTOs(HtmlEmail email, List<EmailAttachmentTO> attachments) throws EmailException {
            for (EmailAttachmentTO attachmentTO : attachments) {
                email.attach(new ByteArrayDataSource(attachmentTO.getBytes(), attachmentTO.getMimeType()),
                        attachmentTO.getName(), attachmentTO.getDescription(),
                        EmailAttachment.ATTACHMENT);
            }
        }
    }
}
