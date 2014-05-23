package be.cegeka.batchers.taxcalculator.batch.config;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.domain.PayCheck;
import be.cegeka.batchers.taxcalculator.application.domain.TaxCalculation;
import be.cegeka.batchers.taxcalculator.infrastructure.config.PersistenceConfig;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.parseInt;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ItemReaderWriterConfig {

    @Autowired
    private PersistenceConfig persistenceConfig;

    @Bean(destroyMethod = "")
    @StepScope
    public JpaPagingItemReader<Employee> taxCalculatorItemReader(@Value("#{stepExecution}") StepExecution stepExecution) {
        JpaPagingItemReader<Employee> employeeItemReader = new JpaPagingItemReader<>();
        employeeItemReader.setEntityManagerFactory(persistenceConfig.entityManagerFactory());
        employeeItemReader.setQueryString(Employee.GET_UNPROCESSED_EMPLOYEES_BY_YEAR_AND_MONTH_QUERY);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("year", parseInt(stepExecution.getJobParameters().getString("year")));
        parameters.put("month", parseInt(stepExecution.getJobParameters().getString("month")));
        parameters.put("jobExecutionId", stepExecution.getJobExecutionId());
        employeeItemReader.setParameterValues(parameters);
        return employeeItemReader;
    }

    @Bean
    public JpaItemWriter<TaxCalculation> taxCalculatorItemWriter() {
        JpaItemWriter<TaxCalculation> employeeJpaItemWriter = new JpaItemWriter<>();
        employeeJpaItemWriter.setEntityManagerFactory(persistenceConfig.entityManagerFactory());
        return employeeJpaItemWriter;
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<TaxCalculation> wsCallItemReader(@Value("#{jobParameters[year]}") Integer year,
                                                                @Value("#{jobParameters[month]}") Integer month) {
        JpaPagingItemReader<TaxCalculation> employeeItemReader = new JpaPagingItemReader<>();
        employeeItemReader.setEntityManagerFactory(persistenceConfig.entityManagerFactory());
        employeeItemReader.setQueryString(TaxCalculation.FIND_BY_YEAR_AND_MONTH_QUERY);
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("month", month);
        queryParams.put("year", year);
        employeeItemReader.setParameterValues(queryParams);
        return employeeItemReader;
    }

    @Bean
    public JpaItemWriter<PayCheck> wsCallItemWriter() {
        JpaItemWriter<PayCheck> employeeJpaItemWriter = new JpaItemWriter<>();
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


