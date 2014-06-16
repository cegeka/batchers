package be.cegeka.batchers.taxcalculator.application.domain.pdf;

public class PDFGenerationException extends Exception {

    public PDFGenerationException(String message) {
        super(message);
    }

    public PDFGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    public PDFGenerationException(Throwable cause) {
        super(cause);
    }

    public PDFGenerationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
