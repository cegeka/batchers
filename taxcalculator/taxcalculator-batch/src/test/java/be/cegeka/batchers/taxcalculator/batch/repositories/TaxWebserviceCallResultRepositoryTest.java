package be.cegeka.batchers.taxcalculator.batch.repositories;

import be.cegeka.batchers.taxcalculator.application.domain.*;
import be.cegeka.batchers.taxcalculator.application.repositories.EmployeeRepository;
import be.cegeka.batchers.taxcalculator.application.service.TaxWebServiceFatalException;
import be.cegeka.batchers.taxcalculator.batch.domain.TaxCalculation;
import be.cegeka.batchers.taxcalculator.batch.domain.TaxCalculationTestBuilder;
import be.cegeka.batchers.taxcalculator.batch.domain.TaxWebserviceCallResult;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

import static be.cegeka.batchers.taxcalculator.application.ApplicationAssertions.assertThat;
import static be.cegeka.batchers.taxcalculator.application.domain.EmployeeTestFixture.anEmployee;

public class TaxWebserviceCallResultRepositoryTest extends AbstractBatchRepositoryIntegrationTest {

    @Autowired
    private TaxWebserviceCallResultRepository taxWebserviceCallResultRepository;

    @Autowired
    private TaxCalculationRepository taxCalculationRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee employee;

    private TaxCalculation january;
    private TaxCalculation february;

    private TaxWebserviceCallResult januaryTry1;
    private TaxWebserviceCallResult januaryTry2;
    private TaxWebserviceCallResult februaryTry1;

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

        januaryTry1 = TaxWebserviceCallResult.callFailed(january, new TaxWebServiceFatalException(new EmployeeTestBuilder().build(), Money.of(CurrencyUnit.EUR, 10), HttpStatus.BAD_REQUEST, null, "boe"));
        januaryTry2 = TaxWebserviceCallResult.callSucceeded(january);
        februaryTry1 = TaxWebserviceCallResult.callSucceeded(february);

        List<TaxWebserviceCallResult> taxWebserviceCallResults = Arrays.asList(januaryTry1, januaryTry2, februaryTry1);
        taxWebserviceCallResults.forEach(taxWebserviceCallResultRepository::save);
    }

    @Test
    public void testFindByTaxCalculation() {
        //ACT
        List<TaxWebserviceCallResult> forJanuary = taxWebserviceCallResultRepository.findByTaxCalculation(january);
        List<TaxWebserviceCallResult> forFebruary = taxWebserviceCallResultRepository.findByTaxCalculation(february);

        //ASSERT
        assertThat(forJanuary).containsOnly(januaryTry1, januaryTry2);
        assertThat(forFebruary).containsOnly(februaryTry1);
    }

    @Test
    public void testFindSuccessfulByTaxCalculation_returnsSuccessful() {
        TaxWebserviceCallResult successfulByTaxCalculation = taxWebserviceCallResultRepository.findSuccessfulByTaxCalculation(january);
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

        TaxWebserviceCallResult successfulByTaxCalculation = taxWebserviceCallResultRepository.findSuccessfulByTaxCalculation(march);
        assertThat(successfulByTaxCalculation).isNull();
    }
}
