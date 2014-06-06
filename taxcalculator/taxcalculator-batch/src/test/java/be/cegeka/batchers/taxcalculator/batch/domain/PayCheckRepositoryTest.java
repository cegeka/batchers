package be.cegeka.batchers.taxcalculator.batch.domain;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.domain.EmployeeRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static be.cegeka.batchers.taxcalculator.application.domain.EmployeeTestFixture.anEmployee;
import static org.fest.assertions.api.Assertions.assertThat;


public class PayCheckRepositoryTest extends AbstractBatchRepositoryIntegrationTest {
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
        byte[] content = getLargeByteArray();
        payCheck = new PayCheckTestBuilder().withTaxCalculation(january).withContent(content).build();
        payCheckRepository.save(payCheck);
    }

    private byte[] getLargeByteArray() {
        return new byte[SIZE_10_MB];
    }

    @Test
    public void testFindByTaxCalculation() throws Exception {
        PayCheck byTaxCalculation = payCheckRepository.findByTaxCalculation(january);

        assertThat(byTaxCalculation).isEqualsToByComparingFields(payCheck);
        assertThat(payCheck.getPayCheckPdf().length).isEqualTo(SIZE_10_MB);
    }
}
