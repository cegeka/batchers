package be.cegeka.batchers.taxcalculator.batch.service;


import be.cegeka.batchers.taxcalculator.batch.api.JobService;
import be.cegeka.batchers.taxcalculator.batch.api.JobStartListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class TaxCalculatorJobService implements JobService {
    @Autowired
    private Job employeeJob;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private ApplicationContext applicationContext;

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

    private void startJobs() {
        try {
            JobParameters jobParameters  = new JobParameters();
            System.out.println("Running job in jobservice");
            jobLauncher.run(employeeJob, jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException
                | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            e.printStackTrace();
            //TODO shouldn't we handle this differently?
        }
    }
}
