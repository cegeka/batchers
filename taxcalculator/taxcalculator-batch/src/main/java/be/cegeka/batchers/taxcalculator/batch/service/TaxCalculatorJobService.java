package be.cegeka.batchers.taxcalculator.batch.service;


import be.cegeka.batchers.taxcalculator.batch.api.JobService;
import be.cegeka.batchers.taxcalculator.batch.api.JobStartListener;
import org.joda.time.DateTime;
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

import java.util.Date;
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
    public void runTaxCalculatorJob() {
        notifyJobStartListeners();
        startJobs();
    }

    private void notifyJobStartListeners() {
        jobStartListeners.stream()
                .forEach(jobStartListener -> jobStartListener.jobHasBeenStarted(employeeJob.getName()));
    }

    protected void startJobs() {
        try {
            JobParameters jobParameters = getNewJobParameters();
            System.out.println("Running job in jobservice");
            jobLauncher.run(employeeJob, jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException
                | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            LOG.error("Job running failed", e);
            //TODO shouldn't we handle this differently?
        }
    }

    protected JobParameters getNewJobParameters() {
        return new JobParametersBuilder()
                .addLong("uniqueIdentifier", new Date().getTime())
                .addLong("month", new Long(new DateTime().getMonthOfYear()), false)
                .addLong("year", new Long(new DateTime().getYear()), false)
                .toJobParameters();
    }
}
