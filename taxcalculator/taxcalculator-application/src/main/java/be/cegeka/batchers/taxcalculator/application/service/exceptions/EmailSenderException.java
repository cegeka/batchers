package be.cegeka.batchers.taxcalculator.application.service.exceptions;

public class EmailSenderException extends Exception {

    public EmailSenderException(String message) {
        super(message);
    }

    public EmailSenderException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailSenderException(Throwable cause) {
        super(cause);
    }

    public EmailSenderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
