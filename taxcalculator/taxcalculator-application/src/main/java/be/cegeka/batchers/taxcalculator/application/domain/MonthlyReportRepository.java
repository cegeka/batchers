package be.cegeka.batchers.taxcalculator.application.domain;

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

    public Money getSuccessSum(int year, int month) {
        Money sum = entityManager.createNamedQuery(MonthlyReport.GET_SUCCESS_SUM, Money.class)
                .setParameter("month", month)
                .setParameter("year", year)
                .getSingleResult();

        return sum == null ? Money.zero(CurrencyUnit.EUR) : sum;
    }

    public Money getFailedSum(int year, int month) {
        Money sum = entityManager.createNamedQuery(MonthlyReport.GET_FAILED_SUM, Money.class)
                .setParameter("month", month)
                .setParameter("year", year)
                .getSingleResult();

        return sum == null ? Money.zero(CurrencyUnit.EUR) : sum;
    }

}
