package be.cegeka.batchers.taxcalculator.batch.service;


import be.cegeka.batchers.taxcalculator.batch.api.JobService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaxCalculatorJobService implements JobService {
    @Autowired
    private Job employeeJob;

    @Autowired
    private SimpleJobLauncher jobLauncher;


    @Override
    public void runTaxCalculatorJob() {
        try {
            JobParameters jobParameters  = new JobParameters();
            System.out.println("Running job in jobservice");
            jobLauncher.run(employeeJob, jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException
                | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            e.printStackTrace();
        }
    }
}
