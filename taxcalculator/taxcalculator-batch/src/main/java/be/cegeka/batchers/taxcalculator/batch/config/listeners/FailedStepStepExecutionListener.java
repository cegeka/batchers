package be.cegeka.batchers.taxcalculator.batch.config.listeners;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class FailedStepStepExecutionListener implements StepExecutionListener {
    @Override
    public void beforeStep(StepExecution stepExecution) {
        //Don't care
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        if(someItemsGotSkippedDueToTaxWebServiceExceptions(stepExecution)) {
            //stepExecution.setStatus(BatchStatus.FAILED);
            return ExitStatus.FAILED;
        }
        return ExitStatus.COMPLETED;
    }

    private boolean someItemsGotSkippedDueToTaxWebServiceExceptions(StepExecution stepExecution) {
        return stepExecution.getReadCount() != stepExecution.getWriteCount();
    }
}
