package be.cegeka.batchers.taxcalculator.application.domain;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Repository
public class MonthlyReportRepository {

    @PersistenceContext
    EntityManager entityManager;

    public MonthlyReport save(MonthlyReport monthlyReport) {
        entityManager.persist(monthlyReport);
        return monthlyReport;
    }

    public MonthlyReport findByYearAndMonth(long year, long month) {
        TypedQuery<MonthlyReport> typedQuery = entityManager.createNamedQuery(MonthlyReport.FIND_BY_YEAR_AND_MONTH, MonthlyReport.class);

        typedQuery.setParameter("year", year);
        typedQuery.setParameter("month", month);

        return typedQuery.getSingleResult();
    }
}
