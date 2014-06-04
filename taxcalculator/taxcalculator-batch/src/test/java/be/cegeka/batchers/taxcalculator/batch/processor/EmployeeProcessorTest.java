package be.cegeka.batchers.taxcalculator.batch.processor;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.domain.EmployeeTestBuilder;
import be.cegeka.batchers.taxcalculator.batch.domain.TaxCalculation;
import be.cegeka.batchers.taxcalculator.application.service.RunningTimeService;
import be.cegeka.batchers.taxcalculator.application.service.TaxCalculatorService;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.StepExecution;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

@RunWith(MockitoJUnitRunner.class)
public class EmployeeProcessorTest {

    @InjectMocks
    private CalculateTaxProcessor calculateTaxProcessor;

    @Mock
    private StepExecution stepExecution;

    @Before
    public void setUp() {
        setInternalState(calculateTaxProcessor, "taxCalculatorService", createTaxCalculatorService());
        setInternalState(calculateTaxProcessor, "year", 2014L);
        setInternalState(calculateTaxProcessor, "month", 5L);

        when(stepExecution.getJobExecutionId()).thenReturn(1L);
    }

    @Test
    public void whenAnEmployeeWithoutCalculatedTax_isProcessed_thenTaxIsOnlyPercentOfCurrentIncome() throws Exception {
        Employee employee1 = new EmployeeTestBuilder().withIncome(1000).withId(1L).build();
        Employee employee2 = new EmployeeTestBuilder().withIncome(1500).withId(1L).build();

        TaxCalculation taxCalculation1 = calculateTaxProcessor.process(employee1);
        TaxCalculation taxCalculation2 = calculateTaxProcessor.process(employee2);

        assertThat(taxCalculation1.getTax()).isEqualTo(Money.of(CurrencyUnit.EUR, 100));
        assertThat(taxCalculation1.getCalculationDate()).isNotNull();

        assertThat(taxCalculation2.getTax()).isEqualTo(Money.of(CurrencyUnit.EUR, 150));
        assertThat(taxCalculation2.getCalculationDate()).isNotNull();
    }

    public TaxCalculatorService createTaxCalculatorService() {
        TaxCalculatorService taxCalculatorService = new TaxCalculatorService();
        taxCalculatorService.setRunningTimeService(mock(RunningTimeService.class));
        return taxCalculatorService;
    }
}
