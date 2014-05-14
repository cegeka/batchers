package be.cegeka.batchers.taxcalculator.application.domain.email;

import org.apache.commons.mail.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.isNoneBlank;

@Service
public class EmailSender {
    private static final Logger logger = LoggerFactory.getLogger(EmailSender.class);
    @Value("${smtp_use_ssl:true}")
    private boolean smtpUseSsl;
    @Value("${smtp_port:465}")
    private int smtpPort;
    @Value("${smtp_server}")
    private String smtpServer;
    @Value("${smtp_user_name}")
    private String smtpUserName;
    @Value("${smtp_user_password}")
    private String smtpPassword;

    public void send(EmailTO emailTO) {
        try {
            Email email = new EmailMapper().mapFromEmailTO(emailTO);
            email.setSSLOnConnect(smtpUseSsl);
            email.setSmtpPort(smtpPort);
            email.setHostName(smtpServer);
            if (isNoneBlank(smtpUserName, smtpPassword)) {
                email.setAuthenticator(new DefaultAuthenticator(smtpUserName, smtpPassword));
            }

            email.send();
        } catch (IllegalArgumentException e) {
            logger.error("Errors occurred while sending the email ", e);
            throw e;
        } catch (EmailException | AddressException | IOException e) {
            logger.error("Errors occurred while sending the email ", e);
            throw new IllegalStateException(e);
        }
    }

    static class EmailMapper {
        private static InternetAddress toInternetAddress(String address) {
            try {
                return new InternetAddress(address);
            } catch (AddressException e) {
                throw new IllegalArgumentException(e);
            }
        }

        public Email mapFromEmailTO(EmailTO emailTO) throws EmailException, AddressException, IOException {
            HtmlEmail email = new HtmlEmail();
            email.setFrom(emailTO.getFrom());
            email.setTo(convertToInternetAddress(emailTO.getTos()));
            email.setSubject(emailTO.getSubject());
            email.setMsg(emailTO.getBody());
            email.setHtmlMsg(emailTO.getBody());
            attachEmailAttachmentTOs(email, emailTO.getAttachments());
            return email;
        }

        private Set<InternetAddress> convertToInternetAddress(List<String> emailAddresses) {
            return emailAddresses.stream().map(EmailMapper::toInternetAddress).collect(toSet());
        }

        private void attachEmailAttachmentTOs(HtmlEmail email, List<EmailAttachmentTO> attachments) throws IOException, EmailException {
            for (EmailAttachmentTO attachmentTO : attachments) {
                email.attach(new ByteArrayDataSource(attachmentTO.getBytes(), attachmentTO.getMimeType()),
                        attachmentTO.getName(), attachmentTO.getDescription(),
                        EmailAttachment.ATTACHMENT);
            }
        }


    }
}
