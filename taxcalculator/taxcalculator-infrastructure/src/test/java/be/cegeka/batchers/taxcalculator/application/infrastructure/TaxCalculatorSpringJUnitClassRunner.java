package be.cegeka.batchers.taxcalculator.application.infrastructure;

import org.junit.runners.model.InitializationError;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

public class TaxCalculatorSpringJUnitClassRunner extends SpringJUnit4ClassRunner {

    public TaxCalculatorSpringJUnitClassRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
    }

    @Override
    protected TestContextManager createTestContextManager(Class<?> clazz) {
        System.setProperty("APP_ENV", "default");
        return super.createTestContextManager(clazz);
    }
}