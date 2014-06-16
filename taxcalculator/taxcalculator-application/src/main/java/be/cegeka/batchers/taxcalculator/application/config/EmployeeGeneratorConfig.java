package be.cegeka.batchers.taxcalculator.application.config;

import be.cegeka.batchers.taxcalculator.application.ApplicationInitializer;
import be.cegeka.batchers.taxcalculator.application.domain.generation.EmployeeGenerator;
import be.cegeka.batchers.taxcalculator.infrastructure.config.PersistenceConfig;
import be.cegeka.batchers.taxcalculator.infrastructure.config.PropertyPlaceHolderConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@Import({PersistenceConfig.class, PropertyPlaceHolderConfig.class})
@ComponentScan(basePackages = "be.cegeka.batchers.taxcalculator.application")
@PropertySource("classpath:taxcalculator-application.properties")
public class EmployeeGeneratorConfig {

    @Autowired
    protected EmployeeGenerator employeeGenerator;

    @Bean
    @Profile("!remotePartitioningSlave")
    public ApplicationInitializer applicationInitializer() {
        ApplicationInitializer applicationInitializer = new ApplicationInitializer();
        applicationInitializer.setGenerateEmployees(true);
        applicationInitializer.setEmployeeGenerator(employeeGenerator);
        return applicationInitializer;
    }
}
