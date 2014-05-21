package be.cegeka.batchers.taxcalculator.batch.config;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.domain.TaxCalculation;
import be.cegeka.batchers.taxcalculator.application.domain.TaxServiceCallResult;
import be.cegeka.batchers.taxcalculator.infrastructure.config.PersistenceConfig;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ItemReaderWriterConfig {

    @Autowired
    private PersistenceConfig persistenceConfig;

    @Bean
    public JpaPagingItemReader<Employee> taxCalculatorItemReader() {
        JpaPagingItemReader<Employee> employeeItemReader = new JpaPagingItemReader<>();
        employeeItemReader.setEntityManagerFactory(persistenceConfig.entityManagerFactory());
        employeeItemReader.setQueryString(Employee.GET_ALL_QUERY);
        return employeeItemReader;
    }

    @Bean
    public JpaItemWriter<Employee> taxCalculatorItemWriter() {
        JpaItemWriter<Employee> employeeJpaItemWriter = new JpaItemWriter<>();
        employeeJpaItemWriter.setEntityManagerFactory(persistenceConfig.entityManagerFactory());
        return employeeJpaItemWriter;
    }

    @Bean
    public JpaPagingItemReader<TaxCalculation> wsCallItemReader() {
        JpaPagingItemReader<TaxCalculation> wsCallItemReader = new JpaPagingItemReader<>();
        wsCallItemReader.setEntityManagerFactory(persistenceConfig.entityManagerFactory());
        wsCallItemReader.setQueryString(TaxCalculation.FIND_BY_YEAR_AND_MONTH_QUERY);
        return wsCallItemReader;
    }

    @Bean
    public JpaItemWriter<TaxServiceCallResult> wsCallItemWriter() {
        JpaItemWriter<TaxServiceCallResult> employeeJpaItemWriter = new JpaItemWriter<>();
        employeeJpaItemWriter.setEntityManagerFactory(persistenceConfig.entityManagerFactory());
        return employeeJpaItemWriter;
    }

    @Bean
    public JpaPagingItemReader<Employee> generatePDFItemReader() {
        JpaPagingItemReader<Employee> employeeItemReader = new JpaPagingItemReader<>();
        employeeItemReader.setEntityManagerFactory(persistenceConfig.entityManagerFactory());
        employeeItemReader.setQueryString(Employee.GET_ALL_QUERY);
        return employeeItemReader;
    }

    @Bean
    public JpaItemWriter<Employee> generatePDFItemWriter() {
        JpaItemWriter<Employee> employeeJpaItemWriter = new JpaItemWriter<>();
        employeeJpaItemWriter.setEntityManagerFactory(persistenceConfig.entityManagerFactory());
        return employeeJpaItemWriter;
    }


}


