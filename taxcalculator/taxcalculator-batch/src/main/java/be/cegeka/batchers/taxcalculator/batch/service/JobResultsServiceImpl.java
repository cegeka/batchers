package be.cegeka.batchers.taxcalculator.batch.service;

import be.cegeka.batchers.taxcalculator.batch.api.JobResultsService;
import be.cegeka.batchers.taxcalculator.batch.config.singlejvm.EmployeeJobConfigSingleJvm;
import be.cegeka.batchers.taxcalculator.batch.domain.JobResult;
import be.cegeka.batchers.taxcalculator.batch.domain.JobStartParams;
import org.joda.time.DateTime;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toMap;

@Service
public class JobResultsServiceImpl implements JobResultsService {
    public static final int NUMBER_OF_MONTHS = 6;
    @Autowired
    JobExecutionMapper jobExecutionMapper;
    @Autowired
    private JobExplorer jobExplorer;
    private Function<JobResult, Integer> onYear = jobResult -> jobResult.getJobStartParams().getYear();
    private Function<JobResult, Integer> onMonth = jobResult -> jobResult.getJobStartParams().getMonth();

    public List<JobResult> getJobResults() {
        List<JobInstance> jobInstancesByJobName = jobExplorer.findJobInstancesByJobName(EmployeeJobConfigSingleJvm.EMPLOYEE_JOB, 0, Integer.MAX_VALUE);

        DateTime currentTime = new DateTime();
        List<JobStartParams> months = getJobStartParamsPreviousMonths(currentTime.getYear(), currentTime.getMonthOfYear());

        final Map<JobStartParams, JobResult> jobResultMap = getMapOfJobResultsForJobInstances(jobInstancesByJobName);

        List<JobResult> collect = months
                .stream()
                .map(mapJobStartParamsToJobResult(jobResultMap))
                .sorted((comparing(onYear).thenComparing(comparing(onMonth))).reversed())
                .collect(Collectors.toList());

        return collect;
    }

    private Map<JobStartParams, JobResult> getMapOfJobResultsForJobInstances(List<JobInstance> jobInstancesByJobName) {
        Map<JobInstance, List<JobExecution>> jobInstanceWithJobExecutions = jobInstancesWithJobExecutionsMap(jobInstancesByJobName);

        List<JobResult> jobResults = jobInstanceWithJobExecutions
                .entrySet().stream().map(jobExecutionMapper::toJobResultTo)
                .collect(Collectors.toList());

        Map<JobStartParams, JobResult> jobStartParamsJobResultMap = mapJobResultsToJobStartParamsWithJobResult(jobResults);

        return jobStartParamsJobResultMap;
    }

    private Map<JobStartParams, JobResult> mapJobResultsToJobStartParamsWithJobResult(List<JobResult> jobResults) {
        return jobResults.stream()
                .collect(Collectors.toMap(jobResult -> jobResult.getJobStartParams(), jobResult -> jobResult));
    }

    private Map<JobInstance, List<JobExecution>> jobInstancesWithJobExecutionsMap(List<JobInstance> jobInstancesByJobName) {
        return jobInstancesByJobName
                .stream()
                .collect(toMap(instance -> instance, instance -> jobExplorer.getJobExecutions(instance)));
    }

    private Function<JobStartParams, JobResult> mapJobStartParamsToJobResult(Map<JobStartParams, JobResult> jobResultMap) {
        return jobStartParams -> {
            JobResult jobResult = jobResultMap.get(jobStartParams);
            if (jobResult == null) {
                jobResult = new JobResult(EmployeeJobConfigSingleJvm.EMPLOYEE_JOB, jobStartParams, new ArrayList<>());
            }
            return jobResult;
        };
    }

    public List<JobStartParams> getJobStartParamsPreviousMonths(int year, int month) {
        List<JobStartParams> entries = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_MONTHS; i++) {
            if (month == 0) {
                month = 12;
                --year;
            }
            JobStartParams jobStart = new JobStartParams(year, month);
            entries.add(jobStart);

            --month;
        }
        return entries;
    }
}
