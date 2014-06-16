package be.cegeka.batchers.taxcalculator.batch.config.skippolicy;

import be.cegeka.batchers.taxcalculator.application.service.exceptions.EmailSenderException;
import be.cegeka.batchers.taxcalculator.application.service.exceptions.TaxWebServiceNonFatalException;
import be.cegeka.batchers.taxcalculator.batch.domain.PayCheck;
import be.cegeka.batchers.taxcalculator.batch.domain.TaxCalculation;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@StepScope
public class MaxConsecutiveExceptionsSkipPolicy implements ItemProcessListener<TaxCalculation, PayCheck>, SkipPolicy {

    @Value("${employeeJob.taxProcessor.retry.maxConsecutiveAttempts:5}")
    private int maxConsecutiveAttempts;

    private int totalSkipLimit = 0;

    @PostConstruct
    public void setInitialTotalSkipLimit() {
        totalSkipLimit = maxConsecutiveAttempts;
    }

    @Override
    public boolean shouldSkip(Throwable t, int skipCount) throws SkipLimitExceededException {
        if (t instanceof TaxWebServiceNonFatalException || t instanceof EmailSenderException) {
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
        totalSkipLimit += maxConsecutiveAttempts;
    }

    @Override
    public void onProcessError(TaxCalculation item, Exception e) {
        //we don't care
    }
}
