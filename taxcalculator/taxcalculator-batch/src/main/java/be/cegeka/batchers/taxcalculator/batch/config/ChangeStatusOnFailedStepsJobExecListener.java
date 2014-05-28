package be.cegeka.batchers.taxcalculator.batch.config;

import org.springframework.batch.core.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ChangeStatusOnFailedStepsJobExecListener implements JobExecutionListener {
    @Override
    public void beforeJob(JobExecution jobExecution) {
        //Don't care
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        List<StepExecution> failedStepExecutions = jobExecution.getStepExecutions()
                .stream()
                .filter(stepExecution -> !stepExecution.getExitStatus().equals(ExitStatus.COMPLETED))
                .collect(Collectors.toList());

        if (failedStepExecutions.size() > 0) {
            jobExecution.setStatus(BatchStatus.FAILED);
        }
    }
}
