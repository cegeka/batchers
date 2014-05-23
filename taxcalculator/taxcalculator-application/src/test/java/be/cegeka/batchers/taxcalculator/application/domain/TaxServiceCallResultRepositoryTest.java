package be.cegeka.batchers.taxcalculator.application.domain;

import be.cegeka.batchers.taxcalculator.application.infrastructure.IntegrationTest;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

import static be.cegeka.batchers.taxcalculator.application.ApplicationAssertions.assertThat;
import static be.cegeka.batchers.taxcalculator.application.domain.EmployeeTestFixture.anEmployee;

public class TaxServiceCallResultRepositoryTest extends IntegrationTest {

    @Autowired
    private TaxServiceCallResultRepository taxServiceCallResultRepository;

    @Autowired
    private TaxCalculationRepository taxCalculationRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee employee;

    private TaxCalculation january;
    private TaxCalculation february;

    private TaxServiceCallResult januaryTry1;
    private TaxServiceCallResult januaryTry2;
    private TaxServiceCallResult februaryTry1;

    @Before
    public void setup() {
        employee = anEmployee();

        employeeRepository.save(employee);

        january = TaxCalculation.from(employee, 2014, 1, Money.of(CurrencyUnit.EUR, 10.0), new DateTime());
        february = TaxCalculation.from(employee, 2014, 2, Money.of(CurrencyUnit.EUR, 10.0), new DateTime());

        List<TaxCalculation> taxCalculations = Arrays.asList(january, february);
        taxCalculations.forEach(tax -> taxCalculationRepository.save(tax));

        januaryTry1 = TaxServiceCallResult.from(january, "", HttpStatus.INTERNAL_SERVER_ERROR.value(), null, DateTime.now());
        januaryTry2 = TaxServiceCallResult.from(january, "", HttpStatus.OK.value(), "", DateTime.now());
        februaryTry1 = TaxServiceCallResult.from(february, "", HttpStatus.OK.value(), "", DateTime.now());

        List<TaxServiceCallResult> taxServiceCallResults = Arrays.asList(januaryTry1, januaryTry2, februaryTry1);
        taxServiceCallResults.forEach(taxServiceCallResult -> taxServiceCallResultRepository.save(taxServiceCallResult));
    }

    @Test
    public void testFindByTaxCalculation() {
        //ACT
        List<TaxServiceCallResult> forJanuary = taxServiceCallResultRepository.findByTaxCalculation(january);
        List<TaxServiceCallResult> forFebruary = taxServiceCallResultRepository.findByTaxCalculation(february);

        //ASSERT
        assertThat(forJanuary).containsOnly(januaryTry1, januaryTry2);
        assertThat(forFebruary).containsOnly(februaryTry1);
    }

    @Test
    @Ignore("Not yet implemented")
    public void testSuccess_Sum() throws Exception {
        Money expectedMoney = Money.of(CurrencyUnit.EUR, 10.0);

        Money actualMoney = taxServiceCallResultRepository.getSuccessSum(2014, 1);

        assertThat(actualMoney).isEqualTo(expectedMoney);
    }
}
