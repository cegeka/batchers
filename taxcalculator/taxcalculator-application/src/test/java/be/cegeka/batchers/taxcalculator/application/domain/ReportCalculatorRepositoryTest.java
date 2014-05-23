package be.cegeka.batchers.taxcalculator.application.domain;

import be.cegeka.batchers.taxcalculator.application.infrastructure.IntegrationTest;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.PersistenceException;
import java.util.Arrays;
import java.util.List;

import static be.cegeka.batchers.taxcalculator.application.domain.EmployeeTestFixture.anEmployee;

public class ReportCalculatorRepositoryTest extends IntegrationTest {

    private static final int SIZE_10MB = 10_000_000;

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private TaxCalculationRepository taxCalculationRepository;
    @Autowired
    private TaxServiceCallResultRepository taxServiceCallResultRepository;
    @Autowired
    private EmployeeRepository payCheckEmployeeRepository;

    @Test
    public void testFindByYearAndMonth() throws Exception {
        Employee gigel = anEmployee();
        Employee ionel = anEmployee();

        employeeRepository.save(gigel);
        employeeRepository.save(ionel);

        Money gigelsTaxAmountForJanuary = Money.of(CurrencyUnit.EUR, 10.0);
        TaxCalculation gigelJanuary = TaxCalculation.from(gigel, 2014, 1, gigelsTaxAmountForJanuary, new DateTime());
        Money gigelsTaxAmountForFebruary = Money.of(CurrencyUnit.EUR, 10.0);
        TaxCalculation gigelFebruary = TaxCalculation.from(gigel, 2014, 2, gigelsTaxAmountForFebruary, new DateTime());

        Money ionelsTaxAmountForJanuary = Money.of(CurrencyUnit.EUR, 12.0);
        TaxCalculation ionelJanuary = TaxCalculation.from(ionel, 2014, 1, ionelsTaxAmountForJanuary, new DateTime());
        Money ionelsTaxAmountForFebruary = Money.of(CurrencyUnit.EUR, 13.0);
        TaxCalculation ionelFebruary = TaxCalculation.from(ionel, 2014, 2, ionelsTaxAmountForFebruary, new DateTime());

        List<TaxCalculation> taxes = Arrays.asList(gigelJanuary, gigelFebruary, ionelJanuary, ionelFebruary);
        taxes.forEach(tax -> taxCalculationRepository.save(tax));


    }

    @Test(expected = PersistenceException.class)
    public void cannotHaveMultipleReportsPerMonth() {

    }

}
