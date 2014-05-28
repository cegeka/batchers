package be.cegeka.batchers.taxcalculator.batch.config.skippolicy;

import be.cegeka.batchers.taxcalculator.application.domain.PayCheck;
import be.cegeka.batchers.taxcalculator.application.domain.TaxCalculation;
import be.cegeka.batchers.taxcalculator.application.service.TaxWebServiceException;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;

public class SkipMax5ConsecutiveNonFatalTaxWebServiceExceptions implements ItemProcessListener<TaxCalculation, PayCheck>, SkipPolicy {

    private int skipLimit = 3;

    private int totalSkipLimit = skipLimit;

    @Override
    public boolean shouldSkip(Throwable t, int skipCount) throws SkipLimitExceededException {
        System.out.println("shouldskip " + skipCount);
        if (t instanceof TaxWebServiceException) {
            if (skipCount >= (totalSkipLimit - 1)) {
                throw new SkipLimitExceededException(totalSkipLimit, t);
            }
            return true;
        }
        return false;
    }


    @Override
    public void beforeProcess(TaxCalculation item) {
        //we don't care
    }

    @Override
    public void afterProcess(TaxCalculation item, PayCheck result) {
        totalSkipLimit += skipLimit;
    }

    @Override
    public void onProcessError(TaxCalculation item, Exception e) {
        System.out.println("onprocesserror");
    }
}
