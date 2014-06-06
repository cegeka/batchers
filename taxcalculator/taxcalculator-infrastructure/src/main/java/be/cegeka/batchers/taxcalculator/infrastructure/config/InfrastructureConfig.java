package be.cegeka.batchers.taxcalculator.infrastructure.config;

import com.google.common.eventbus.EventBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfrastructureConfig {

    @Bean
    public EventBus eventBus() {
        return new EventBus();
    }

    @Bean
    public EventBusPostProcessor eventBusPostProcessor() {
        return new EventBusPostProcessor();
    }
}
