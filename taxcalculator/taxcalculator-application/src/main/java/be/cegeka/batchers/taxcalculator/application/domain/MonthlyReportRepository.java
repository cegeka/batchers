package be.cegeka.batchers.taxcalculator.application.domain;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;

@Repository
@Transactional(readOnly = true, isolation = Isolation.DEFAULT)
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

    public void deleteAll() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaDelete<MonthlyReport> criteriaDelete = criteriaBuilder.createCriteriaDelete(MonthlyReport.class);

        criteriaDelete.from(MonthlyReport.class);

        entityManager.createQuery(criteriaDelete).executeUpdate();
    }
}
