package be.cegeka.batchers.taxcalculator.presentation.websockets;

import be.cegeka.batchers.taxcalculator.batch.api.JobService;
import be.cegeka.batchers.taxcalculator.batch.api.events.JobEvent;
import be.cegeka.batchers.taxcalculator.batch.api.events.JobProgressEvent;
import com.google.common.eventbus.Subscribe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
public class GreetingController {

    @Autowired
    private JobService jobService;

    @Autowired
    private MessageSendingOperations<String> messagingTemplate;

    @Subscribe
    public void onJobEvent(JobEvent jobEvent) {
        notifyEvent(jobEvent);
    }

    @Subscribe
    public void onJobProgressEvent(JobProgressEvent jobProgressEvent) {
        this.messagingTemplate.convertAndSend("/jobinfo-updates", jobProgressEvent);
    }

    public void notifyEvent(JobEvent jobEvent) {
        this.messagingTemplate.convertAndSend("/jobinfo-updates", jobEvent);
    }


}
