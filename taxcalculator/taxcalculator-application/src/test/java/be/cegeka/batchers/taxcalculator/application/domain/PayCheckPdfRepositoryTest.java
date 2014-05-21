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

import static be.cegeka.batchers.taxcalculator.application.domain.EmployeeTestFixture.anEmployee;
import static org.fest.assertions.api.Assertions.assertThat;


public class PayCheckPdfRepositoryTest extends IntegrationTest {
    public static final int SIZE_10_MB = 10_000_000;
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    TaxCalculationRepository taxCalculationRepository;
    @Autowired
    PayCheckPdfRepository payCheckPdfRepository;


    private Employee employee;
    private TaxCalculation january;
    private TaxCalculation february;
    private PayCheckPdf payCheckPdf;

    @Before
    public void setUp() throws Exception {
        employee = anEmployee();

        employeeRepository.save(employee);

        january = TaxCalculation.from(employee, 2014, 1, Money.of(CurrencyUnit.EUR, 10.0), new DateTime());
        february = TaxCalculation.from(employee, 2014, 2, Money.of(CurrencyUnit.EUR, 10.0), new DateTime());

        List<TaxCalculation> taxCalculations = Arrays.asList(january, february);
        taxCalculations.forEach(tax -> taxCalculationRepository.save(tax));
        byte[] content = getLargeByteArray();
        payCheckPdf = PayCheckPdf.from(january, content);
        payCheckPdfRepository.save(payCheckPdf);
    }

    private byte[] getLargeByteArray() {
        return new byte[SIZE_10_MB];
    }

    @Test
    public void testFindByTaxCalculation() throws Exception {
        PayCheckPdf byTaxCalculation = payCheckPdfRepository.findByTaxCalculation(january);

        assertThat(byTaxCalculation).isEqualTo(payCheckPdf);
        assertThat(payCheckPdf.getContent().length).isEqualTo(SIZE_10_MB);
    }
}
