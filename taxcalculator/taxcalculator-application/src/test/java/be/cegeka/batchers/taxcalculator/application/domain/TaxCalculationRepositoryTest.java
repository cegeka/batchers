package be.cegeka.batchers.taxcalculator.application.domain;

import be.cegeka.batchers.taxcalculator.application.infrastructure.IntegrationTest;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
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

        gigelJanuary = TaxCalculation.from(1L, gigel, 2014, 1, Money.of(CurrencyUnit.EUR, 10.0));
        gigelFebruary = TaxCalculation.from(1L, gigel, 2014, 2, Money.of(CurrencyUnit.EUR, 10.0));

        ionelJanuary = TaxCalculation.from(1L, ionel, 2014, 1, Money.of(CurrencyUnit.EUR, 12.0));
        ionelFebruary = TaxCalculation.from(1L, ionel, 2014, 2, Money.of(CurrencyUnit.EUR, 13.0));

        List<TaxCalculation> taxes = Arrays.asList(gigelJanuary, gigelFebruary, ionelJanuary, ionelFebruary);
        taxes.forEach(taxCalculationRepository::save);

    }

    @Test
    public void testFind() {
        //ACT
        List<TaxCalculation> byYearAndMonth = taxCalculationRepository.find(2014L, 1L, 1L);

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
        TaxCalculation gigelJanuary2 = TaxCalculation.from(1L, gigel, 2014, 1, Money.of(CurrencyUnit.EUR, 15.0));
        taxCalculationRepository.save(gigelJanuary2);
    }

}
