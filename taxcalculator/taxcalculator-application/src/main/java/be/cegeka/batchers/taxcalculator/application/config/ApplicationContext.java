package be.cegeka.batchers.taxcalculator.application.config;

import be.cegeka.batchers.taxcalculator.application.ApplicationInitializer;
import be.cegeka.batchers.taxcalculator.application.domain.EmployeeGenerator;
import be.cegeka.batchers.taxcalculator.infrastructure.config.PersistenceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@Import(PersistenceConfig.class)
@ComponentScan(basePackages = "be.cegeka.batchers.taxcalculator.application")
public class ApplicationContext {

    @Autowired
    protected EmployeeGenerator employeeGenerator;

    @Bean
    public ApplicationInitializer applicationInitializer() {
        ApplicationInitializer applicationInitializer = new ApplicationInitializer();
        applicationInitializer.setGenerateEmployees(true);
        applicationInitializer.setEmployeeGenerator(employeeGenerator);
        return applicationInitializer;
    }
}
