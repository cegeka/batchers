package be.cegeka.batchers.taxcalculator.application.domain;

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
}
