package be.cegeka.batchers.taxcalculator.application.domain.reporting;

import be.cegeka.batchers.taxcalculator.application.domain.AbstractRepository;
import be.cegeka.batchers.taxcalculator.application.domain.reporting.MonthlyReport;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

@Repository
@Transactional(readOnly = true, isolation = Isolation.DEFAULT)
public class MonthlyReportRepository extends AbstractRepository<MonthlyReport> {

    public MonthlyReport findByYearAndMonth(int year, int month) {
        TypedQuery<MonthlyReport> typedQuery = entityManager.createNamedQuery(MonthlyReport.FIND_BY_YEAR_AND_MONTH, MonthlyReport.class);

        typedQuery.setParameter("year", year);
        typedQuery.setParameter("month", month);

        return typedQuery.getSingleResult();
    }

    public MonthlyReport findById(Long id) {
        TypedQuery<MonthlyReport> typedQuery = entityManager.createNamedQuery(MonthlyReport.FIND_BY_ID, MonthlyReport.class);

        typedQuery.setParameter(MonthlyReport.ID, id);

        MonthlyReport monthlyReport;
        try {
            monthlyReport = typedQuery.getSingleResult();

        } catch (NoResultException ex) {
            monthlyReport = null;
        }
        return monthlyReport;
    }
}
