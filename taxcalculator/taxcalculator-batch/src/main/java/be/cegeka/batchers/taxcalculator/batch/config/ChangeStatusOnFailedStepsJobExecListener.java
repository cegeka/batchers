package be.cegeka.batchers.taxcalculator.batch.config;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ChangeStatusOnFailedStepsJobExecListener extends JobExecutionListenerSupport {

    @Override
    public void afterJob(JobExecution jobExecution) {
        List<StepExecution> failedStepExecutions = jobExecution.getStepExecutions()
                .stream()
                .filter(stepExecution -> !stepExecution.getExitStatus().equals(ExitStatus.COMPLETED))
                .collect(Collectors.toList());

        if (failedStepExecutions.size() > 0) {
            jobExecution.setStatus(BatchStatus.FAILED);
            jobExecution.setExitStatus(new ExitStatus("FAILED-BECAUSE-OF-SKIPS"));
        }
    }
}
