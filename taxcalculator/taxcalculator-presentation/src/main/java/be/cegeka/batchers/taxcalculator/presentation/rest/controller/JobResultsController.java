package be.cegeka.batchers.taxcalculator.presentation.rest.controller;

import be.cegeka.batchers.taxcalculator.application.domain.reporting.MonthlyReport;
import be.cegeka.batchers.taxcalculator.application.domain.reporting.MonthlyReportRepository;
import be.cegeka.batchers.taxcalculator.batch.domain.JobResult;
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
    private JobResultsService jobResultsService;
    @Autowired
    private MonthlyReportRepository monthlyReportRepository;

    @RequestMapping(value = "jobResults", method = RequestMethod.GET)
    @ResponseBody
    public List<JobResult> getJobResults() {
        return jobResultsService.getJobResults();
    }

    @RequestMapping(value = "files/job_report/{id}.pdf", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> getJobReportPdf(@PathVariable("id") Long jobExecutionId) {
        MonthlyReport monthlyReport = monthlyReportRepository.findById(jobExecutionId);

        if (monthlyReport != null) {
            return new ResponseEntity<>(monthlyReport.getMonthlyReportPdf(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
