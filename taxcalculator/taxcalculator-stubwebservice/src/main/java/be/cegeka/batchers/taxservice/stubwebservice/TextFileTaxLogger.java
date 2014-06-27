package be.cegeka.batchers.taxservice.stubwebservice;

import com.google.common.collect.EvictingQueue;
import com.google.common.collect.Queues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Queue;

@Component
public class TextFileTaxLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(TextFileTaxLogger.class);

    private final Queue<String> logs = Queues.synchronizedQueue(EvictingQueue.create(100));

    public void log(Long employeeId, double taxAmount, String status) {
        String msg = "Received tax request for employee with id " + employeeId + " with amount " + taxAmount + " and completed with status: " + status;
        logs.add(msg);
        LOGGER.info(msg);
    }

    public String getLogs(){
        StringBuilder sb = new StringBuilder();
        logs.stream().forEach(line -> sb.append(line).append("\n"));
        return sb.toString();
    }

    public void clear(){
        logs.clear();
    }
}
