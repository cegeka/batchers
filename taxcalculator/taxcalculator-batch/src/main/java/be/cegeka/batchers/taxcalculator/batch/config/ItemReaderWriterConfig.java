package be.cegeka.batchers.taxcalculator.batch.config;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.domain.MonthlyTaxForEmployee;
import be.cegeka.batchers.taxcalculator.application.domain.PayCheck;
import be.cegeka.batchers.taxcalculator.application.domain.TaxCalculation;
import be.cegeka.batchers.taxcalculator.infrastructure.config.PersistenceConfig;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
        parameters.put("year", stepExecution.getJobParameters().getLong("year").intValue());
        parameters.put("month", stepExecution.getJobParameters().getLong("month").intValue());
        parameters.put("jobExecutionId", stepExecution.getJobExecutionId());
        employeeItemReader.setParameterValues(parameters);
        return employeeItemReader;
    }

    @Bean(destroyMethod = "")
    @StepScope
    public JpaPagingItemReader<Employee> taxCalculatorItemReaderSlave(@Value("#{stepExecution}") StepExecution stepExecution) {
        JpaPagingItemReader<Employee> employeeItemReader = new JpaPagingItemReader<>();
        employeeItemReader.setEntityManagerFactory(persistenceConfig.entityManagerFactory());
        employeeItemReader.setQueryString(Employee.GET_UNPROCESSED_EMPLOYEES_BY_YEAR_AND_MONTH_QUERY_SLAVE);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("year", stepExecution.getJobParameters().getLong("year").intValue());
        parameters.put("month", stepExecution.getJobParameters().getLong("month").intValue());
        parameters.put("jobExecutionId", stepExecution.getJobExecutionId());
        parameters.put("minId", stepExecution.getExecutionContext().getLong("minValue"));
        parameters.put("maxId", stepExecution.getExecutionContext().getLong("maxValue"));
        employeeItemReader.setParameterValues(parameters);
        return employeeItemReader;
    }

    @Bean
    public JpaItemWriter<TaxCalculation> taxCalculatorItemWriter() {
        JpaItemWriter<TaxCalculation> employeeJpaItemWriter = new JpaItemWriter<>();
        employeeJpaItemWriter.setEntityManagerFactory(persistenceConfig.entityManagerFactory());
        return employeeJpaItemWriter;
    }

    @Bean(destroyMethod = "")
    @StepScope
    public JpaPagingItemReader<TaxCalculation> wsCallItemReader(@Value("#{jobParameters[year]}") Long year, @Value("#{jobParameters[month]}") Long month, @Value("#{stepExecution}") StepExecution stepExecution) {
        JpaPagingItemReader<TaxCalculation> employeeItemReader = new JpaPagingItemReader<>();
        employeeItemReader.setEntityManagerFactory(persistenceConfig.entityManagerFactory());
        employeeItemReader.setQueryString(TaxCalculation.FIND_BY_YEAR_AND_MONTH_QUERY);
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("year", year.intValue());
        queryParams.put("month", month.intValue());
        queryParams.put("jobExecutionId", stepExecution.getJobExecutionId());
        employeeItemReader.setParameterValues(queryParams);
        return employeeItemReader;
    }

    @Bean
    public JpaItemWriter<PayCheck> wsCallItemWriter() {
        JpaItemWriter<PayCheck> employeeJpaItemWriter = new JpaItemWriter<>();
        employeeJpaItemWriter.setEntityManagerFactory(persistenceConfig.entityManagerFactory());
        return employeeJpaItemWriter;
    }

    @Bean(destroyMethod = "")
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

    @Bean
    public JpaItemWriter<MonthlyTaxForEmployee> monthlyTaxForEmployeeJpaItemWriter() {
        JpaItemWriter<MonthlyTaxForEmployee> monthlyTaxForEmployeeJpaItemWriter = new JpaItemWriter<>();
        monthlyTaxForEmployeeJpaItemWriter.setEntityManagerFactory(persistenceConfig.entityManagerFactory());
        return monthlyTaxForEmployeeJpaItemWriter;
    }

}
