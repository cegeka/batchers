package be.cegeka.batchers.taxcalculator.presentation.rest.controller;

import be.cegeka.batchers.taxcalculator.batch.api.JobService;
import be.cegeka.batchers.taxcalculator.batch.api.events.JobStartRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class JobRestController {
    private static final Logger LOG = LoggerFactory.getLogger(JobRestController.class);
    @Autowired
    JobService jobService;

    @RequestMapping(value = "runJob/{year}/{month}", method = RequestMethod.POST)
    @ResponseBody
    public void runJob(@PathVariable("year") Long year, @PathVariable("month") Long month) {
        LOG.debug("Running job in rest controller");
        jobService.runTaxCalculatorJob(new JobStartRequest("employeeJob", year.intValue(), month.intValue()));
    }

}
