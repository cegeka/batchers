package be.cegeka.batchers.taxcalculator.batch.integration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;


@Configuration
@Profile("test")
public class TaskExecutorTestConfig {

    @Bean
    public TaskExecutor taskExecutor() {
        return new SyncTaskExecutor();
    }
}
