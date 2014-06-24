package be.cegeka.batchers.taxcalculator.application.infrastructure;


import org.fest.assertions.api.AbstractAssert;
import org.fest.assertions.api.Assertions;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

import java.util.List;

public class SmtpAssertions extends AbstractAssert<SmtpAssertions, Wiser> {

    public SmtpAssertions(Wiser actual) {
        super(actual, SmtpAssertions.class);
    }

    public SmtpAssertions hasReceivedMessages(int numberOfMessages) {
        Assertions.assertThat(actual.getMessages()).hasSize(numberOfMessages);
        return this;
    }

    public SmtpAssertions hasReceivedMessageContaining(String text) {
        List<WiserMessage> messages = actual.getMessages();
        for (WiserMessage message : messages) {
            if (new String(message.getData()).contains(text)) {
                return this;
            }
        }
        Assertions.fail("Could not find a message containt text '" + text + "'");
        return this;
    }

    public SmtpAssertions hasReceivedMessageSentTo(String receiver) {
        List<WiserMessage> messages = actual.getMessages();
        for (WiserMessage message : messages) {
            if (message.getEnvelopeReceiver().equals(receiver)) {
                return this;
            }
        }
        Assertions.fail("Could not find a message containt sent to '" + receiver + "'");
        return this;
    }

    public SmtpAssertions hasReceivedMessageSentFrom(String sender) {
        List<WiserMessage> messages = actual.getMessages();
        for (WiserMessage message : messages) {
            if (message.getEnvelopeSender().equals(sender)) {
                return this;
            }
        }
        Assertions.fail("Could not find a message containt sent to '" + sender + "'");
        return this;
    }

    public SmtpAssertions hasNoReceivedMessages() {
        hasReceivedMessages(0);
        return this;
    }
}
