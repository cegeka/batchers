package be.cegeka.batchers.taxcalculator.application.domain;

import be.cegeka.batchers.taxcalculator.application.infrastructure.IntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.PersistenceException;
import java.util.Arrays;
import java.util.List;

import static be.cegeka.batchers.taxcalculator.application.ApplicationAssertions.assertThat;
import static be.cegeka.batchers.taxcalculator.application.domain.EmployeeTestFixture.anEmployee;

public class TaxCalculationRepositoryTest extends IntegrationTest {

    @Autowired
    private TaxCalculationRepository taxCalculationRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee gigel;
    private Employee ionel;
    private TaxCalculation gigelJanuary;
    private TaxCalculation gigelFebruary;
    private TaxCalculation ionelJanuary;
    private TaxCalculation ionelFebruary;

    @Before
    public void setup() {
        gigel = anEmployee();
        ionel = anEmployee();

        employeeRepository.save(gigel);
        employeeRepository.save(ionel);

        gigelJanuary = new TaxCalculationTestBuilder().withEmployee(gigel).withMonth(1).withTax(10.0).build();
        gigelFebruary = new TaxCalculationTestBuilder().withEmployee(gigel).withMonth(2).withTax(10.0).build();

        ionelJanuary = new TaxCalculationTestBuilder().withEmployee(ionel).withMonth(1).withTax(12.0).build();
        ionelFebruary = new TaxCalculationTestBuilder().withEmployee(ionel).withMonth(2).withTax(13.0).build();

        List<TaxCalculation> taxes = Arrays.asList(gigelJanuary, gigelFebruary, ionelJanuary, ionelFebruary);
        taxes.forEach(taxCalculationRepository::save);
    }

    @Test
    public void testFind() {
        //ACT
        List<TaxCalculation> byYearAndMonth = taxCalculationRepository.find(2014, 1, 1L);

        //ASSERT
        assertThat(byYearAndMonth).containsOnly(gigelJanuary, ionelJanuary);
    }

    @Test
    public void testFindByEmployee() {
        //ACT
        List<TaxCalculation> byEmployee = taxCalculationRepository.findByEmployee(ionel);

        //ASSERT
        assertThat(byEmployee).containsOnly(ionelJanuary, ionelFebruary);
    }

    @Test(expected = PersistenceException.class)
    public void cannotHaveDuplicateCalculation() {
        TaxCalculation gigelJanuary2 = new TaxCalculationTestBuilder().withEmployee(gigel).withMonth(1).withTax(15.0).build();
        taxCalculationRepository.save(gigelJanuary2);
    }

}
