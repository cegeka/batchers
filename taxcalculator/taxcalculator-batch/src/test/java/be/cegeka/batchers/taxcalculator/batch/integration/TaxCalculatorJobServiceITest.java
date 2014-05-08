package be.cegeka.batchers.taxcalculator.batch.integration;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.domain.EmployeeBuilder;
import be.cegeka.batchers.taxcalculator.application.domain.EmployeeRepository;
import be.cegeka.batchers.taxcalculator.batch.service.TaxCalculatorJobService;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.fest.assertions.api.Assertions.assertThat;

public class TaxCalculatorJobServiceITest extends AbstractIntegrationTest {
    @Autowired
    TaxCalculatorJobService taxCalculatorJobService;

    @Autowired
    private EmployeeRepository employeeRepository;


    @Test
    public void testJobService() {
        Employee employee = new EmployeeBuilder()
                .withFirstName("Monica")
                .withIncome(1000)
                .build();

        employeeRepository.save(employee);
        taxCalculatorJobService.runTaxCalculatorJob();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        Employee reloadedEmployee = employeeRepository.getBy(employee.getId());
        assertThat(reloadedEmployee.getTaxTotal()).isEqualTo(Money.of(CurrencyUnit.EUR, 100));
        assertThat(reloadedEmployee.getCalculationDate()).isEqualTo(DateTime.now());
    }
}
