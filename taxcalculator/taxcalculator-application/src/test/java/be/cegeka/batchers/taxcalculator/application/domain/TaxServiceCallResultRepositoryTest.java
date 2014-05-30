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


        january = new TaxCalculationTestBuilder()
                .withEmployee(employee)
                .withMonth(1)
                .withTax(10.0)
                .build();

        february = new TaxCalculationTestBuilder()
                .withEmployee(employee)
                .withMonth(2)
                .withTax(10.0)
                .build();

        List<TaxCalculation> taxCalculations = Arrays.asList(january, february);
        taxCalculations.forEach(taxCalculationRepository::save);

        januaryTry1 = TaxServiceCallResult.from(january, "", HttpStatus.INTERNAL_SERVER_ERROR.value(), null, DateTime.now(), false);
        januaryTry2 = TaxServiceCallResult.from(january, "", HttpStatus.OK.value(), "", DateTime.now(), true);
        februaryTry1 = TaxServiceCallResult.from(february, "", HttpStatus.OK.value(), "", DateTime.now(), true);

        List<TaxServiceCallResult> taxServiceCallResults = Arrays.asList(januaryTry1, januaryTry2, februaryTry1);
        taxServiceCallResults.forEach(taxServiceCallResultRepository::save);
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
    @Ignore
    public void testSuccess_Sum() throws Exception {
        Employee anotherEmployee = anEmployee();
        employeeRepository.save(anotherEmployee);
        TaxCalculation january = new TaxCalculationTestBuilder()
                .withEmployee(anotherEmployee)
                .withMonth(1)
                .withTax(10.0)
                .build();
        taxCalculationRepository.save(january);
        TaxServiceCallResult januaryTry2 = TaxServiceCallResult.from(january, "", HttpStatus.OK.value(), "", DateTime.now(), true);
        taxServiceCallResultRepository.save(januaryTry2);


        Money expectedMoney = Money.of(CurrencyUnit.EUR, 20.0);

        Money actualMoney = taxServiceCallResultRepository.getSuccessSum(2014, 1);

        assertThat(actualMoney).isEqualTo(expectedMoney);
    }

    @Test
    public void testFailed_Sum() {
        Employee anotherEmployee = anEmployee();
        employeeRepository.save(anotherEmployee);
        TaxCalculation january = new TaxCalculationTestBuilder()
                .withEmployee(anotherEmployee)
                .withMonth(1)
                .withTax(10.0)
                .build();
        taxCalculationRepository.save(january);
        TaxServiceCallResult januaryTry2 = TaxServiceCallResult.from(january, "", HttpStatus.BAD_REQUEST.value(), "", DateTime.now(), false);
        taxServiceCallResultRepository.save(januaryTry2);

        Money expectedMoney = Money.of(CurrencyUnit.EUR, 20.0);

        Money actualMoney = taxServiceCallResultRepository.getFailedSum(2014, 1);

        assertThat(actualMoney).isEqualTo(expectedMoney);
    }

    @Test
    public void testFindSuccessfulByTaxCalculation_returnsSuccessful() {
        TaxServiceCallResult successfulByTaxCalculation = taxServiceCallResultRepository.findSuccessfulByTaxCalculation(january);
        assertThat(successfulByTaxCalculation).isEqualTo(januaryTry2);
    }

    @Test
    public void testFindSuccessfulByTaxCalculation_returnsNullWhenNoCall() {
        TaxCalculation march = new TaxCalculationTestBuilder()
                .withEmployee(employee)
                .withMonth(3)
                .withTax(10.0)
                .build();
        ;
        taxCalculationRepository.save(march);

        TaxServiceCallResult successfulByTaxCalculation = taxServiceCallResultRepository.findSuccessfulByTaxCalculation(march);
        assertThat(successfulByTaxCalculation).isNull();
    }
}
