package be.cegeka.batchers.taxcalculator.batch.api;

import be.cegeka.batchers.taxcalculator.batch.domain.JobResult;

import java.util.List;

public interface JobResultsService {

    public List<JobResult> getJobResults();
}
