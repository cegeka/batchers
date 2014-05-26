package be.cegeka.batchers.taxcalculator.batch.service;

import be.cegeka.batchers.taxcalculator.batch.config.EmployeeJobConfig;
import be.cegeka.batchers.taxcalculator.to.JobResultTo;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobResultsService {
    @Autowired
    private JobExplorer jobExplorer;
    @Autowired
    JobExecutionMapper jobExecutionMapper;

    public List<JobResultTo> getFinishedJobResults() {
        List<JobInstance> jobInstancesByJobName = jobExplorer.getJobInstancesByJobName(EmployeeJobConfig.EMPLOYEE_JOB, 0, Integer.MAX_VALUE);

        return
                jobInstancesByJobName.stream().
                        flatMap(instance -> jobExplorer.getJobExecutions(instance).stream())
                        .map(jobExecutionMapper::toJobResultTo)
                        .collect(Collectors.toList());
    }
}
