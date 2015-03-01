package be.cegeka.batchers.taxcalculator.presentation.websockets;

import be.cegeka.batchers.taxcalculator.batch.api.JobService;
import be.cegeka.batchers.taxcalculator.batch.api.events.JobEvent;
import be.cegeka.batchers.taxcalculator.batch.api.events.JobStatusEvent;
import be.cegeka.batchers.taxcalculator.batch.api.events.JobProgressEvent;
import com.google.common.eventbus.Subscribe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @Autowired
    private JobService jobService;

    @Autowired
    private MessageSendingOperations<String> messagingTemplate;

    @Subscribe
    public void onJobEvent(JobEvent jobEvent) {
        this.messagingTemplate.convertAndSend("/jobinfo-updates", jobEvent);
    }
}
