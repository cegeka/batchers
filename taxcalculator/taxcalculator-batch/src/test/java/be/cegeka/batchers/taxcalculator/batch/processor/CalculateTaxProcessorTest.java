package be.cegeka.batchers.taxcalculator.batch.processor;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.domain.EmployeeTestBuilder;
import be.cegeka.batchers.taxcalculator.application.service.TaxCalculatorService;
import be.cegeka.batchers.taxcalculator.batch.domain.TaxCalculation;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.StepExecution;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

@RunWith(MockitoJUnitRunner.class)
public class CalculateTaxProcessorTest {

    @InjectMocks
    private CalculateTaxProcessor calculateTaxProcessor;

    @Mock
    private TaxCalculatorService taxCalculatorService;

    @Mock
    private StepExecution stepExecution;

    @Before
    public void setUpCalculateTaxProcessor() {
    }

    @Test
    public void testProcess() throws Exception {
        //GIVEN
        setInternalState(calculateTaxProcessor, "year", 2014);
        setInternalState(calculateTaxProcessor, "month", 5);
        setInternalState(calculateTaxProcessor, "stepExecution", stepExecution);

        when(stepExecution.getJobExecutionId()).thenReturn(123L);

        Employee employee = new EmployeeTestBuilder().withIncome(100).build();

        //WHEN
        TaxCalculation taxCalculation = calculateTaxProcessor.process(employee);

        //THEN
        assertThat(taxCalculation.getJobExecutionId()).isEqualTo(123L);
        assertThat(taxCalculation.getEmployee()).isEqualTo(employee);
        assertThat(taxCalculation.getYear()).isEqualTo(2014);
        assertThat(taxCalculation.getMonth()).isEqualTo(5);
        assertThat(taxCalculation.getTax()).isEqualTo(Money.of(CurrencyUnit.EUR, 10));
    }
}
