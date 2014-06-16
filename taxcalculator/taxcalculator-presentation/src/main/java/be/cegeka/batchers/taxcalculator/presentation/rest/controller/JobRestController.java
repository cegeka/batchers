package be.cegeka.batchers.taxcalculator.presentation.rest.controller;

import be.cegeka.batchers.taxcalculator.batch.api.JobService;
import be.cegeka.batchers.taxcalculator.batch.domain.JobStartParams;
import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Controller
public class JobRestController {
    private static final Logger LOG = LoggerFactory.getLogger(JobRestController.class);
    @Autowired
    private JobService jobService;

    private Lock lock = new ReentrantLock();

    @RequestMapping(value = "runJob/{year}/{month}", method = RequestMethod.POST)
    @ResponseBody
    public void runJob(@PathVariable("year") Long year, @PathVariable("month") Long month) {
        if(lock.tryLock()){
          try{
            LOG.debug("Running job in rest controller");
            Stopwatch stopwatch = Stopwatch.createStarted();
            jobService.runTaxCalculatorJob(new JobStartParams(year, month));
            stopwatch.stop();

            LOG.info("=======================================");
            LOG.info("Time needed: " + stopwatch.elapsed(TimeUnit.SECONDS));
            LOG.info("=======================================");
          } finally {
            lock.unlock();
          }
        } else {
          LOG.info("A job is currently running. Try again later.");
        }
    }

}
