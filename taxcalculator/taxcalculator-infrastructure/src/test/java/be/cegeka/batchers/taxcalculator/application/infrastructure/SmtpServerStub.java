package be.cegeka.batchers.taxcalculator.application.infrastructure;

import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

import java.util.List;

public class SmtpServerStub {

    private static Wiser wiser;

    static {
        wiser = new Wiser();
        wiser.setPort(2500);
    }

    public static void start() {
        wiser.start();
    }

    public static void stop() {
        wiser.getMessages().clear();
        wiser.stop();
    }

    public static List<WiserMessage> getMessages() {
        return wiser.getMessages();
    }

    public static void clearMessages() {
        wiser.getMessages().clear();
    }


    public static Wiser wiser() {
        return wiser;
    }

}
