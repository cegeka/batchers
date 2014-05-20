package be.cegeka.batchers.taxcalculator.application.domain;

import be.cegeka.batchers.taxcalculator.application.infrastructure.IntegrationTest;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
    private TaxCalculation gigel_january;
    private TaxCalculation gigel_february;
    private TaxCalculation ionel_january;
    private TaxCalculation ionel_february;

    @Before
    public void setup() {
        gigel = anEmployee();
        ionel = anEmployee();

        employeeRepository.save(gigel);
        employeeRepository.save(ionel);

        gigel_january = TaxCalculation.from(gigel, 2014, 1, Money.of(CurrencyUnit.EUR, 10.0), new DateTime());
        gigel_february = TaxCalculation.from(gigel, 2014, 2, Money.of(CurrencyUnit.EUR, 10.0), new DateTime());

        ionel_january = TaxCalculation.from(ionel, 2014, 1, Money.of(CurrencyUnit.EUR, 12.0), new DateTime());
        ionel_february = TaxCalculation.from(ionel, 2014, 2, Money.of(CurrencyUnit.EUR, 13.0), new DateTime());

        List<TaxCalculation> taxes = Arrays.asList(gigel_january, gigel_february, ionel_january, ionel_february);
        taxes.forEach(tax -> taxCalculationRepository.save(tax));

    }

    @Test
    public void testFindByYearAndMonth() {
        //ACT
        List<TaxCalculation> byYearAndMonth = taxCalculationRepository.findByYearAndMonth(2014, 1);

        //ASSERT
        assertThat(byYearAndMonth).containsOnly(gigel_january, ionel_january);
    }

    @Test
    public void testFindByEmployee() {
        //ACT
        List<TaxCalculation> byEmployee = taxCalculationRepository.findByEmployee(ionel);

        //ASSERT
        assertThat(byEmployee).containsOnly(ionel_january, ionel_february);
    }

}
