package be.cegeka.batchers.taxcalculator.batch.config.remotepartitioning;

import be.cegeka.batchers.taxcalculator.application.service.EmployeeService;
import be.cegeka.batchers.taxcalculator.batch.api.events.JobStatusEvent;
import be.cegeka.batchers.taxcalculator.batch.api.events.JobProgressEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import org.springframework.batch.core.BatchStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Profile(value = {"remotePartitioningMaster"})
public class JobProgressNotifier implements MessageListener<JobProgressEvent> {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ClusterConfig clusterConfig;
    @Autowired
    private EventBus eventBus;

    private AtomicInteger progressCount = new AtomicInteger(0);
    private Long employeeCount;

    @PostConstruct
    public void listen() {
        clusterConfig.jobProgressEventsTopic().addMessageListener(this);
        eventBus.register(this);
    }

    @Subscribe
    public void onJobEvent(JobStatusEvent jobProgressEvent) {
        if (jobProgressEvent.getStatus() == BatchStatus.STARTED.name()) {
            employeeCount = employeeService.getEmployeeCount();
            progressCount = new AtomicInteger(0);
        }
    }

    @Override
    public void onMessage(Message<JobProgressEvent> message) {
        JobProgressEvent slaveProgressEvent = message.getMessageObject();

        int itemsDone = progressCount.addAndGet(slaveProgressEvent.getPercentageComplete());

        int progress = (int) (itemsDone / (employeeCount * 1.0) * 100);
        JobProgressEvent updateProgress = new JobProgressEvent(slaveProgressEvent.getJobStartParams(), slaveProgressEvent.getStepName(), progress);
        eventBus.post(updateProgress);
    }
}
