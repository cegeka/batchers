package be.cegeka.batchers.taxcalculator.batch.config.listeners;

import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.StepExecutionListener;

public interface JobProgressListener extends StepExecutionListener, ItemWriteListener {
}
