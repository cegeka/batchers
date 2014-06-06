package be.cegeka.batchers.taxcalculator.application.domain.reporting;

import be.cegeka.batchers.taxcalculator.application.infrastructure.IntegrationTest;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.fest.assertions.api.Assertions.assertThat;

public class MonthlyReportRepositoryTest extends IntegrationTest {

    private static final int SIZE_10MB = 10_000_000;

    @Autowired
    private MonthlyReportRepository monthlyReportRepository;

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


}
