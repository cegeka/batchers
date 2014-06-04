package be.cegeka.batchers.taxcalculator.batch.config.listeners;

import com.google.common.eventbus.EventBus;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class JobProgressListener implements ChunkListener {

    private int percentageComplete = 0;

    @Autowired
    private EventBus eventBus;

    @Override
    public void beforeChunk(ChunkContext context) {
    }

    @Override
    public void afterChunk(ChunkContext context) {
        context.getStepContext().getStepName();
        context.getStepContext().getJobParameters();
        context.getStepContext().getStepExecution().getWriteCount();
        context.getStepContext().getAttribute("");
    }

    @Override
    public void afterChunkError(ChunkContext context) {

    }
}
