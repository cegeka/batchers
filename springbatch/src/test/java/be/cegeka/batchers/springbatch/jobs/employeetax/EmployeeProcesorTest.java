package be.cegeka.batchers.springbatch.jobs.employeetax;

import be.cegeka.batchers.springbatch.domain.Employee;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by andreip on 29.04.2014.
 */
public class EmployeeProcesorTest {

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

        assertEquals("processed employee tax is not equal to given one", (int) (income1 * 0.1), employee1.getTaxTotal());
        DateTime calculationDate1 = employee1.getCalculationDate();
        assertTrue("tax calculation date is wrong", interval.contains(calculationDate1));

        assertEquals("processed employee tax is not equal to given one", (int) (income2 * 0.1), employee2.getTaxTotal());
        DateTime calculationDate2 = employee2.getCalculationDate();
        assertTrue("tax calculation date is wrong", interval.contains(calculationDate2));
    }

    @Test
    public void whenAnEmployeeWithPreviousTax_isProcessed_thenTaxOnCurrentIncomeIsAddedToTotalTax() throws Exception {
        int income1 = 1000;
        Employee employee1 = new Employee();
        employee1.setIncome(income1);
        int taxTotal = 549;
        employee1.setTaxTotal(taxTotal);
        employee1.setCalculationDate(DateTime.now().minusMonths(1));

        employee1 = employeeProcessor.process(employee1);

        assertEquals("processed employee tax is not correct", (int) (taxTotal + income1 * 0.1),
                employee1.getTaxTotal());
        DateTime calculationDate1 = employee1.getCalculationDate();
        assertTrue("tax calculation date is wrong", interval.contains(calculationDate1));
    }

    @Test
    public void whenAnAlreadyProcessedEmployee_isProcessed_thenTaxIsSame() {
        Employee employee = new Employee();
        int taxTotal = 12345;
        employee.setTaxTotal(taxTotal);
        employee.setIncome(2000);
        DateTime calculationDate = employee.getCalculationDate();

        employee = employeeProcessor.process(employee);

        assertEquals("tax should not change", taxTotal, employee.getTaxTotal());
        assertEquals("calculation date should not change", calculationDate, employee.getCalculationDate());

    }
}
