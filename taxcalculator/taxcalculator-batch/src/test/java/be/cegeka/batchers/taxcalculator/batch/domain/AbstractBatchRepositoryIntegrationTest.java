package be.cegeka.batchers.taxcalculator.batch.domain;

import be.cegeka.batchers.taxcalculator.application.config.EmployeeGeneratorTestConfig;
import be.cegeka.batchers.taxcalculator.application.infrastructure.TaxCalculatorSpringJUnitClassRunner;
import be.cegeka.batchers.taxcalculator.infrastructure.config.PersistenceConfig;
import be.cegeka.batchers.taxcalculator.infrastructure.config.PropertyPlaceHolderConfig;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(TaxCalculatorSpringJUnitClassRunner.class)
@TransactionConfiguration(defaultRollback = true)
@ContextConfiguration(classes = {AbstractBatchRepositoryIntegrationTest.TestConfiguration.class})
@Transactional
public abstract class AbstractBatchRepositoryIntegrationTest {

    @Configuration
    @Import(value = {EmployeeGeneratorTestConfig.class, PropertyPlaceHolderConfig.class, PersistenceConfig.class,})
    @ComponentScan(basePackages = {"be.cegeka.batchers.taxcalculator.batch.domain"})
    @PropertySource("classpath:taxcalculator-application.properties")
    static class TestConfiguration {
    }

}
