package be.cegeka.batchers.taxcalculator.batch.service;

import be.cegeka.batchers.taxcalculator.batch.config.EmployeeJobConfig;
import be.cegeka.batchers.taxcalculator.batch.domain.JobResult;
import be.cegeka.batchers.taxcalculator.batch.domain.JobStartParams;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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

    public List<JobResult> getJobResults() {
        List<JobInstance> jobInstancesByJobName = jobExplorer.getJobInstancesByJobName(EmployeeJobConfig.EMPLOYEE_JOB, 0, Integer.MAX_VALUE);

        List<Long> months = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L);

        final Map<Long, JobResult> jobResultMap =
                jobInstancesByJobName
                        .stream()
                        .collect(toMap(instance -> instance, instance -> jobExplorer.getJobExecutions(instance)))
                        .entrySet().stream().map(jobExecutionMapper::toJobResultTo)
                        .collect(Collectors.toMap(jobResult -> jobResult.getMonth(), jobResult -> jobResult));

        List<JobResult> collect = months
                .stream()
                .map(month -> {
                    JobResult jobResult = jobResultMap.get(month);
                    if (jobResult == null) {
                        jobResult = new JobResult(EmployeeJobConfig.EMPLOYEE_JOB, new JobStartParams(2014L, month), new ArrayList<>());
                    }
                    jobResult.setMonth(month);
                    return jobResult;
                })
                .sorted((comparing(onYear).thenComparing(comparing(onMonth))))
                .collect(Collectors.toList());

        return collect;
    }
}
