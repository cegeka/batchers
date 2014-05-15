package be.cegeka.batchers.taxcalculator.batch.config;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.batch.CalculateTaxProcessor;
import be.cegeka.batchers.taxcalculator.batch.CallWebserviceProcessor;
import be.cegeka.batchers.taxcalculator.batch.SendPaycheckProcessor;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class ProcessorConfig {

    @Autowired
    private CalculateTaxProcessor calculateTaxProcessor;
    @Autowired
    private CallWebserviceProcessor callWebserviceProcessor;
    @Autowired
    private SendPaycheckProcessor sendPaycheckProcessor;

    @Bean
    public CompositeItemProcessor<Employee, Employee> processor() {
        CompositeItemProcessor<Employee, Employee> employeeEmployeeCompositeItemProcessor = new CompositeItemProcessor<>();
        employeeEmployeeCompositeItemProcessor.setDelegates(Arrays.asList(
                calculateTaxProcessor,
                callWebserviceProcessor,
                sendPaycheckProcessor
        ));
        return employeeEmployeeCompositeItemProcessor;
    }

}
