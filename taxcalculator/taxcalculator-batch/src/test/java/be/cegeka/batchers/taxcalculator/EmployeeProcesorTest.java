package be.cegeka.batchers.taxcalculator;

import be.cegeka.batchers.taxcalculator.domain.Employee;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * Created by andreip on 29.04.2014.
 */
public class EmployeeProcesorTest {

    public static final double DELTA = 1e-15;
    private EmployeeProcessor employeeProcessor;
    private Interval interval;

    @Before
    public void setUp() {
        employeeProcessor = new EmployeeProcessor();

        DateTime now = new DateTime();
        LocalDate today = now.toLocalDate();
        LocalDate tomorrow = today.plusDays(1);
        interval = new Interval(today.toDateTimeAtStartOfDay(), tomorrow.toDateTimeAtStartOfDay());
    }

    @Test
    public void whenAnEmployeeWithoutCalculatedTax_isProcessed_thenTaxIsOnlyPercentOfCurrentIncome() throws Exception {
        int income1 = 1000;
        int income2 = 1500;
        Employee employee1 = new Employee();
        employee1.setIncome(income1);
        Employee employee2 = new Employee();
        employee2.setIncome(income2);

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
        int income = 1000;

        Employee employee = new Employee();
        employee.setIncome(income);
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
        Employee employee = new Employee();

        employee.setIncome(2000);
        employee.addTax();
        DateTime calculationDate = employee.getCalculationDate();

        employee = employeeProcessor.process(employee);

        assertEquals("tax should not change", employee.getIncomeTax(), employee.getTaxTotal().getAmount().doubleValue(), DELTA);
        assertEquals("calculation date should not change", calculationDate, employee.getCalculationDate());

    }

    @Test
    public void givenIncome_whenGetIncomeTax_thenReturnCorrectIncome() {
        Employee employee = new Employee();
        employee.setIncome(2000);

        double incomeTax = employee.getIncomeTax();

        assertEquals("incomeTax is wrong", 200d, incomeTax, DELTA);
    }
}
