package be.cegeka.batchers.taxcalculator.application.domain;

import be.cegeka.batchers.taxcalculator.application.infrastructure.IntegrationTest;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
        MonthlyReport march = MonthlyReport.from(3L, 2014, 3, new byte[SIZE_10MB], DateTime.now());
        MonthlyReport april = MonthlyReport.from(4L, 2014, 4, new byte[SIZE_10MB], DateTime.now());

        monthlyReportRepository.save(march);
        monthlyReportRepository.save(april);

        MonthlyReport foundMarch = monthlyReportRepository.findByYearAndMonth(2014, 3);
        MonthlyReport foundApril = monthlyReportRepository.findByYearAndMonth(2014, 4);

        assertThat(foundMarch).isEqualTo(march);
        assertThat(foundMarch.getMonthlyReportPdf().length).isEqualTo(SIZE_10MB);
        assertThat(foundApril).isEqualTo(april);
        assertThat(foundApril.getMonthlyReportPdf().length).isEqualTo(SIZE_10MB);
    }

    @Test
    public void testFindById() {
        MonthlyReport report1 = MonthlyReport.from(3L, 2014, 3, new byte[SIZE_10MB], DateTime.now());
        MonthlyReport report2 = MonthlyReport.from(4L, 2014, 4, new byte[SIZE_10MB], DateTime.now());
        monthlyReportRepository.save(report1);
        monthlyReportRepository.save(report2);

        MonthlyReport searchedForReport = monthlyReportRepository.findById(report2.getId());

        assertThat(searchedForReport.getId()).isEqualTo(report2.getId());
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
