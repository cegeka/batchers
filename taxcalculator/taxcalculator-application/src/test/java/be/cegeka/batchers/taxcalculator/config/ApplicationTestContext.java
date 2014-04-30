package be.cegeka.batchers.taxcalculator.config;

import be.cegeka.batchers.taxcalculator.application.ApplicationInitializer;
import org.springframework.context.annotation.Bean;

public class ApplicationTestContext extends ApplicationContext {

    @Bean
    @Override
    public ApplicationInitializer applicationInitializer() {
        ApplicationInitializer applicationInitializer = super.applicationInitializer();
        applicationInitializer.setGenerateEmployees(false);

        return applicationInitializer;
    }
}
