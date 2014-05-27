package be.cegeka.batchers.taxcalculator.batch.service;

import be.cegeka.batchers.taxcalculator.batch.config.EmployeeJobConfig;
import be.cegeka.batchers.taxcalculator.batch.domain.JobResult;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toMap;

@Service
public class JobResultsService {
    @Autowired
    JobExecutionMapper jobExecutionMapper;
    @Autowired
    private JobExplorer jobExplorer;
    private Function<JobResult, Integer> onYear = jobResult -> jobResult.getJobStartParams().getYear();
    private Function<JobResult, Integer> onMonth = jobResult -> jobResult.getJobStartParams().getMonth();

    public List<JobResult> getFinishedJobResults() {
        List<JobInstance> jobInstancesByJobName = jobExplorer.getJobInstancesByJobName(EmployeeJobConfig.EMPLOYEE_JOB, 0, Integer.MAX_VALUE);

        return jobInstancesByJobName
                .stream()
                .collect(toMap(instance -> instance, instance -> jobExplorer.getJobExecutions(instance)))
                .entrySet().stream().map(jobExecutionMapper::toJobResultTo)
                .sorted((comparing(onYear).thenComparing(comparing(onMonth))).reversed())
                .collect(Collectors.toList());
    }
}
