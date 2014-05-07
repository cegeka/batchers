package be.cegeka.batchers.taxcalculator.batch;

import be.cegeka.batchers.taxcalculator.domain.Employee;
import be.cegeka.batchers.taxcalculator.domain.EmployeeBuilder;
import be.cegeka.batchers.taxcalculator.domain.EmployeeRepository;
import be.cegeka.batchers.taxcalculator.service.RunningTimeService;
import be.cegeka.batchers.taxcalculator.service.TaxCalculatorService;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class EmployeeProcessorTest {

    public static final double DELTA = 1e-15;
    private Interval interval;

    @InjectMocks
    private EmployeeProcessor employeeProcessor;

    @Before
    public void setUp() {
        employeeProcessor.taxCalculatorService = createTaxCalculatorService();
        DateTime now = new DateTime();
        LocalDate today = now.toLocalDate();
        LocalDate tomorrow = today.plusDays(1);
        interval = new Interval(today.toDateTimeAtStartOfDay(), tomorrow.toDateTimeAtStartOfDay());
    }

    @Test
    public void whenAnEmployeeWithoutCalculatedTax_isProcessed_thenTaxIsOnlyPercentOfCurrentIncome() throws Exception {
        Employee employee1 = new EmployeeBuilder()
                .withIncome(1000)
                .build();
        Employee employee2 = new EmployeeBuilder()
                .withIncome(1500)
                .build();

        assertNull("employee should have empty calculation date", employee1.getCalculationDate());
        assertNull("employee should have empty calculation date", employee2.getCalculationDate());

        employee1 = employeeProcessor.process(employee1);
        employee2 = employeeProcessor.process(employee2);

        assertEquals("processed employee tax is not equal to given one", employee1.getIncomeTax(), employee1.getTaxTotal().getAmount().doubleValue(), DELTA);
        DateTime calculationDate1 = employee1.getCalculationDate();
        assertTrue("tax calculation date is wrong", interval.contains(calculationDate1));

        assertEquals("processed employee tax is not equal to given one", employee2.getIncomeTax(), employee2.getTaxTotal().getAmount().doubleValue(), DELTA);
        DateTime calculationDate2 = employee2.getCalculationDate();
        assertTrue("tax calculation date is wrong", interval.contains(calculationDate2));
    }

    @Test
    public void whenAnEmployeeWithPreviousTax_isProcessed_thenTaxOnCurrentIncomeIsAddedToTotalTax() throws Exception {

        Employee employee = new EmployeeBuilder()
                .withIncome(1000)
                .build();

        employee.addTax();
        employee.setCalculationDate(DateTime.now().minusMonths(1));

        employee = employeeProcessor.process(employee);

        double totalComputedTax = BigDecimal.valueOf(employee.getIncomeTax() * 2).doubleValue();
        assertEquals("processed employee tax is not correct", totalComputedTax,
                employee.getTaxTotal().getAmount().doubleValue(), DELTA);
        DateTime calculationDate = employee.getCalculationDate();
        assertTrue("tax calculation date is wrong", interval.contains(calculationDate));
    }

    @Test
    public void whenAnAlreadyProcessedEmployee_isProcessed_thenTaxIsSame() {
        Employee employee = new EmployeeBuilder()
                .withIncome(2000)
                .build();

        employee.addTax();
        DateTime calculationDate = employee.getCalculationDate();

        employee = employeeProcessor.process(employee);

        assertEquals("tax should not change", employee.getIncomeTax(), employee.getTaxTotal().getAmount().doubleValue(), DELTA);
        assertEquals("calculation date should not change", calculationDate, employee.getCalculationDate());

    }

    @Test
    public void givenIncome_whenGetIncomeTax_thenReturnCorrectIncome() {
        Employee employee = new EmployeeBuilder()
                .withIncome(2000)
                .build();

        double incomeTax = employee.getIncomeTax();

        assertEquals("incomeTax is wrong", 200d, incomeTax, DELTA);
    }

    public TaxCalculatorService createTaxCalculatorService() {
        TaxCalculatorService taxCalculatorService = new TaxCalculatorService();
        taxCalculatorService.setRunningTimeService(mock(RunningTimeService.class));
        return taxCalculatorService;
    }
}
