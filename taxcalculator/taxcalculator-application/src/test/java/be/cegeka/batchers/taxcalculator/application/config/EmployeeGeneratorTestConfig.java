package be.cegeka.batchers.taxcalculator.application.config;

import be.cegeka.batchers.taxcalculator.application.ApplicationInitializer;
import org.springframework.context.annotation.Bean;

public class EmployeeGeneratorTestConfig extends EmployeeGeneratorConfig {

    @Bean
    @Override
    public ApplicationInitializer applicationInitializer() {
        ApplicationInitializer applicationInitializer = super.applicationInitializer();
        applicationInitializer.setGenerateEmployees(false);

        return applicationInitializer;
    }

    @Bean
    public String smtp_username() {
        return "username";
    }

    @Bean
    public String smtp_password() {
        return "password";
    }

    @Bean
    public String smtp_port() {
        return "2500";
    }

    @Bean
    public String smtp_server() {
        return "localhost";
    }
}
