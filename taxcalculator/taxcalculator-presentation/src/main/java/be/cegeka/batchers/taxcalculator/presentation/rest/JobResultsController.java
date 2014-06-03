package be.cegeka.batchers.taxcalculator.presentation.rest;

import be.cegeka.batchers.taxcalculator.application.domain.MonthlyReport;
import be.cegeka.batchers.taxcalculator.application.domain.MonthlyReportRepository;
import be.cegeka.batchers.taxcalculator.batch.domain.JobExecutionResult;
import be.cegeka.batchers.taxcalculator.batch.service.JobResultsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class JobResultsController {
    @Autowired
    MonthlyReportRepository monthlyReportRepository;
    @Autowired
    private JobResultsService jobResultsService;

    @RequestMapping(value = "jobResults", method = RequestMethod.GET)
    @ResponseBody
    public List<JobExecutionResult> getJobResults() {
        return jobResultsService.getJobResults();
    }

    @RequestMapping(value = "files/job_report/{jobId}.pdf", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> getJobReportPdf(@PathVariable("jobId") Long jobExecutionId) {
        MonthlyReport monthlyReport = monthlyReportRepository.findByJobExecutionId(jobExecutionId);

        if (monthlyReport != null) {
            return new ResponseEntity<>(monthlyReport.getMontlyReportPdf(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
