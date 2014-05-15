package be.cegeka.batchers.taxcalculator.application.domain.email;

import java.util.LinkedList;
import java.util.List;

public class EmailTO {
    private List<String> tos = new LinkedList<String>();
    private List<EmailAttachmentTO> attachments = new LinkedList<EmailAttachmentTO>();
    private String body;
    private String subject;
    private String from;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public List<String> getTos() {
        return tos;
    }

    public void addTo(String email) {
        this.tos.add(email);
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<EmailAttachmentTO> getAttachments() {
        return attachments;
    }

    public void addAttachment(EmailAttachmentTO attachmentTO) {
        attachments.add(attachmentTO);
    }
}
