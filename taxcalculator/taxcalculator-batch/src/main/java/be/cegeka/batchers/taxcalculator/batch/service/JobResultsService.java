package be.cegeka.batchers.taxcalculator.batch.service;

import be.cegeka.batchers.taxcalculator.batch.config.EmployeeJobConfig;
import be.cegeka.batchers.taxcalculator.batch.domain.JobExecutionResult;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JobResultsService {
    @Autowired
    JobExecutionMapper jobExecutionMapper;
    @Autowired
    private JobExplorer jobExplorer;

    public List<JobExecutionResult> getJobResults() {
        List<JobInstance> jobInstancesByJobName = jobExplorer.getJobInstancesByJobName(EmployeeJobConfig.EMPLOYEE_JOB, 0, Integer.MAX_VALUE);

        List<JobExecution> allJobExecutions = new ArrayList<>();

        jobInstancesByJobName.forEach(jobInstance -> allJobExecutions.addAll(jobExplorer.getJobExecutions(jobInstance)));

        return jobExecutionMapper.getJobExecutionResults(allJobExecutions);
    }


}
