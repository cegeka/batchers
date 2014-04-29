package be.cegeka.batchers.taxservice;

/**
 * Created with IntelliJ IDEA.
 * User: raduci
 * Date: 29.04.2014
 * Time: 14:25
 * To change this template use File | Settings | File Templates.
 */
public interface TaxSubmissionLogger {

    void log(TaxTo taxTo,String status);
}
