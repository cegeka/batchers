package be.cegeka.batchers.taxcalculator.application.domain;

import be.cegeka.batchers.taxcalculator.application.infrastructure.IntegrationTest;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static be.cegeka.batchers.taxcalculator.application.domain.EmployeeTestFixture.anEmployee;
import static org.fest.assertions.api.Assertions.assertThat;


public class PayCheckRepositoryTest extends IntegrationTest {
    public static final int SIZE_10_MB = 10_000_000;
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    TaxCalculationRepository taxCalculationRepository;
    @Autowired
    PayCheckRepository payCheckRepository;

    private TaxCalculation january;
    private TaxCalculation february;
    private PayCheck payCheck;

    @Before
    public void setUp() throws Exception {
        Employee employee = anEmployee();

        employeeRepository.save(employee);

        january = TaxCalculation.from(1L, employee, 2014, 1, Money.of(CurrencyUnit.EUR, 10.0));
        february = TaxCalculation.from(1L, employee, 2014, 2, Money.of(CurrencyUnit.EUR, 10.0));

        List<TaxCalculation> taxCalculations = Arrays.asList(january, february);
        taxCalculations.forEach(taxCalculationRepository::save);
        byte[] content = getLargeByteArray();
        payCheck = PayCheck.from(january, content);
        payCheckRepository.save(payCheck);
    }

    private byte[] getLargeByteArray() {
        return new byte[SIZE_10_MB];
    }

    @Test
    public void testFindByTaxCalculation() throws Exception {
        PayCheck byTaxCalculation = payCheckRepository.findByTaxCalculation(january);

        assertThat(byTaxCalculation).isEqualTo(payCheck);
        assertThat(payCheck.getPayCheckPdf().length).isEqualTo(SIZE_10_MB);
    }
}
