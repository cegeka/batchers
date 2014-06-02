package be.cegeka.batchers.taxcalculator.application.domain;

import be.cegeka.batchers.taxcalculator.application.infrastructure.IntegrationTest;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.PersistenceException;

import static be.cegeka.batchers.taxcalculator.application.domain.EmployeeTestFixture.anEmployee;
import static org.fest.assertions.api.Assertions.assertThat;

public class MonthlyReportRepositoryTest extends IntegrationTest {

    private static final int SIZE_10MB = 10_000_000;

    @Autowired
    private MonthlyReportRepository monthlyReportRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private TaxCalculationRepository taxCalculationRepository;
    @Autowired
    private PayCheckRepository payCheckRepository;

    @Test
    public void testFindByYearAndMonth() throws Exception {
        MonthlyReport march = MonthlyReport.from(2014, 3, new byte[SIZE_10MB], DateTime.now());
        MonthlyReport april = MonthlyReport.from(2014, 4, new byte[SIZE_10MB], DateTime.now());

        monthlyReportRepository.save(march);
        monthlyReportRepository.save(april);

        MonthlyReport foundMarch = monthlyReportRepository.findByYearAndMonth(2014L, 3L);
        MonthlyReport foundApril = monthlyReportRepository.findByYearAndMonth(2014L, 4L);

        assertThat(foundMarch).isEqualTo(march);
        assertThat(foundMarch.getMontlyReportPdf().length).isEqualTo(SIZE_10MB);
        assertThat(foundApril).isEqualTo(april);
        assertThat(foundApril.getMontlyReportPdf().length).isEqualTo(SIZE_10MB);
    }

    @Test(expected = PersistenceException.class)
    public void givenTwoReportsWithSameJobExecutionId_whenSaveIsCalled_thenAnExceptionIsThrown() {
        Long jobExecutionId = 12345L;
        MonthlyReport report1 = MonthlyReport.from(2014, 3, new byte[SIZE_10MB], DateTime.now(), jobExecutionId);
        MonthlyReport report2 = MonthlyReport.from(2014, 4, new byte[SIZE_10MB], DateTime.now(), jobExecutionId);
        monthlyReportRepository.save(report1);
        monthlyReportRepository.save(report2);

    }

    @Test
    public void testFindByJobExecutionId() {
        Long jobExecutionId = 12345L;
        MonthlyReport report1 = MonthlyReport.from(2014, 3, new byte[SIZE_10MB], DateTime.now(), jobExecutionId);
        MonthlyReport report2 = MonthlyReport.from(2014, 4, new byte[SIZE_10MB], DateTime.now(), jobExecutionId + 1);
        monthlyReportRepository.save(report1);
        monthlyReportRepository.save(report2);

        MonthlyReport searchedForReport = monthlyReportRepository.findByJobExecutionId(jobExecutionId);

        assertThat(report1.getId()).isEqualTo(searchedForReport.getId());
    }

    @Test
    public void givenNoEmployees_whenGettingSum_theSumIsZero() {
        assertThat(monthlyReportRepository.getSuccessSum(2014, 5)).isEqualTo(Money.zero(CurrencyUnit.EUR));
        assertThat(monthlyReportRepository.getFailedSum(2014, 5)).isEqualTo(Money.zero(CurrencyUnit.EUR));
    }


    @Test
    public void givenOneEmployeeWithTaxAndPaycheck_whenGettingSum_theSumIsCorrect() {
        //ARRANGE
        Employee employee = anEmployee();
        employeeRepository.save(employee);

        TaxCalculation tax = new TaxCalculationTestBuilder()
                .withEmployee(employee)
                .build();
        taxCalculationRepository.save(tax);

        PayCheck payCheck = PayCheck.from(1l, tax, null);
        payCheckRepository.save(payCheck);

        //ACT
        Money successSum = monthlyReportRepository.getSuccessSum(tax.getYear(), tax.getMonth());
        Money failedSum = monthlyReportRepository.getFailedSum(tax.getYear(), tax.getMonth());

        //ASSERT
        assertThat(successSum).isEqualTo(tax.getTax());
        assertThat(failedSum).isEqualTo(Money.zero(CurrencyUnit.EUR));
    }

    @Test
    public void givenOneEmployeeWithTaxAndNoPaycheck_whenGettingSum_theSumIsCorrect() {
        //ARRANGE
        Employee employee = anEmployee();
        employeeRepository.save(employee);

        TaxCalculation tax = new TaxCalculationTestBuilder()
                .withEmployee(employee)
                .build();
        taxCalculationRepository.save(tax);

        //ACT
        Money successSum = monthlyReportRepository.getSuccessSum(tax.getYear(), tax.getMonth());
        Money failedSum = monthlyReportRepository.getFailedSum(tax.getYear(), tax.getMonth());

        //ASSERT
        assertThat(successSum).isEqualTo(Money.zero(CurrencyUnit.EUR));
        assertThat(failedSum).isEqualTo(tax.getTax());
    }

}
