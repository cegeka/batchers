package be.cegeka.batchers.taxcalculator.presentation.websockets;

import be.cegeka.batchers.taxcalculator.batch.api.JobService;
import be.cegeka.batchers.taxcalculator.batch.api.events.JobEvent;
import be.cegeka.batchers.taxcalculator.batch.api.events.JobStartRequest;
import com.google.common.eventbus.Subscribe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

@Controller
public class GreetingController implements ApplicationListener {

    @Autowired
    private JobService jobService;

    @Autowired
    private MessageSendingOperations<String> messagingTemplate;

    @Subscribe
    public void onJobEvent(JobEvent jobEvent) {
        notifyEvent(jobEvent);
    }

//    @MessageMapping("/launch-job")
//    public void greeting(TestMessage message) throws Exception {
//        jobService.runTaxCalculatorJob(new JobStartRequest("employeeJob", 2014, 6));
//    }

    public void notifyEvent(JobEvent jobEvent) {
        this.messagingTemplate.convertAndSend("/jobinfo-updates", jobEvent);
    }

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
//        if(applicationEvent instanceof SessionConnectedEvent) {
//            JobEvent jobEvent = jobService.getTaxCalculatorJobStatus(new JobStartRequest("employeeJob", 2014, 6));
//            notifyEvent(jobEvent);
//        }
        System.out.println(applicationEvent);
    }
}
