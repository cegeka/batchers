package be.cegeka.batchers.taxcalculator.batch.service;

import be.cegeka.batchers.taxcalculator.batch.config.singlejvm.EmployeeJobConfigSingleJvm;
import be.cegeka.batchers.taxcalculator.batch.domain.JobResult;
import be.cegeka.batchers.taxcalculator.batch.domain.JobStartParams;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
public class JobResultsService {
    @Autowired
    private JobExecutionMapper jobExecutionMapper;
    @Autowired
    private JobExplorer jobExplorer;

    public List<JobResult> getJobResults() {
        final Map<JobInstance, List<JobExecution>> jobInstanceWithJobExecutions = getJobInstanceWithJobExecutions();
        List<JobResult> executedJobResultsForFirstHalfOf2014 = getAsExecutedJobResultsAndFilterForFirstHalfOf2014(jobInstanceWithJobExecutions);
        List<JobResult> allJobResultsForFirstHalfOf2014 = addNonExecutedJobResultsForFirstHalfOf2014(executedJobResultsForFirstHalfOf2014);

        return allJobResultsForFirstHalfOf2014;
    }

    private Map<JobInstance, List<JobExecution>> getJobInstanceWithJobExecutions() {
        List<JobInstance> jobInstancesByJobName = jobExplorer.getJobInstancesByJobName(EmployeeJobConfigSingleJvm.EMPLOYEE_JOB, 0, Integer.MAX_VALUE);
        return jobInstancesByJobName
                .stream()
                .collect(toMap(instance -> instance, instance -> jobExplorer.getJobExecutions(instance)));
    }

    private List<JobResult> getAsExecutedJobResultsAndFilterForFirstHalfOf2014(Map<JobInstance, List<JobExecution>> jobInstanceWithJobExecutions) {
        return jobInstanceWithJobExecutions
                .entrySet().stream()
                .map(jobExecutionMapper::toJobResult)
                .filter(jobsInFirstHalfOf2014())
                .collect(toList());
    }

    private List<JobResult> addNonExecutedJobResultsForFirstHalfOf2014(List<JobResult> executedJobResultsInFirstHalfOf2014) {
        List<JobResult> result = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            JobResult jobResultForMonth = executedJobResultsInFirstHalfOf2014.stream()
                    .filter(jobsIn2014ForMonth(i))
                    .findFirst()
                    .orElse(new JobResult(EmployeeJobConfigSingleJvm.EMPLOYEE_JOB, new JobStartParams(2014, i)));
            result.add(jobResultForMonth);
        }

        return result;
    }

    private Predicate<? super JobResult> jobsInFirstHalfOf2014() {
        return jobResult -> jobResult.getJobStartParams().getYear() == 2014 && jobResult.getJobStartParams().getMonth() >= 1 && jobResult.getJobStartParams().getMonth() <= 6;
    }

    private Predicate<? super JobResult> jobsIn2014ForMonth(int month) {
        return jobResult -> jobResult.getJobStartParams().getYear() == 2014 && jobResult.getJobStartParams().getMonth() == month;
    }
}
