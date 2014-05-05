package be.cegeka.batchers.taxservice;

public interface TaxSubmissionLogger {

    void log(TaxTo taxTo,String status);
}
