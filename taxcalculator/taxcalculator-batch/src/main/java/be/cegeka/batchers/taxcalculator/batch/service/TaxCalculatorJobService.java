package be.cegeka.batchers.taxcalculator.batch.service;


import be.cegeka.batchers.taxcalculator.batch.api.JobService;
import be.cegeka.batchers.taxcalculator.batch.api.JobStartListener;
import be.cegeka.batchers.taxcalculator.batch.domain.JobStartParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class TaxCalculatorJobService implements JobService {
    private static final Logger LOG = LoggerFactory.getLogger(TaxCalculatorJobService.class);

    @Autowired
    private Job employeeJob;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired(required = false)
    private Set<JobStartListener> jobStartListeners = new HashSet<>();

    @Override
    public void runTaxCalculatorJob(JobStartParams jobStartParams) {
        notifyJobStartListeners();
        startJobs(jobStartParams.getYear(), jobStartParams.getMonth());
    }

    private void notifyJobStartListeners() {
        jobStartListeners.stream()
                .forEach(jobStartListener -> jobStartListener.jobHasBeenStarted(employeeJob.getName()));
    }

    protected void startJobs(long year, long month) {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("month", month)
                    .addLong("year", year)
                    .toJobParameters();

            System.out.println("Running job in jobservice");
            jobLauncher.run(employeeJob, jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException
                | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            LOG.error("Job running failed", e);
            //TODO shouldn't we handle this differently?
        }
    }
}
