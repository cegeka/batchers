package be.cegeka.batchers.taxcalculator.application.domain;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import java.util.List;

@Repository
@Transactional(readOnly = true, isolation = Isolation.DEFAULT)
public class TaxCalculationRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Transactional
    public void save(TaxCalculation taxCalculation) {
        entityManager.persist(taxCalculation);
    }

    public TaxCalculation getBy(Long id) {
        return entityManager.find(TaxCalculation.class, id);
    }

    public List<TaxCalculation> findByYearAndMonth(long year, long month) {
        TypedQuery<TaxCalculation> byMonthAndYear = entityManager.createNamedQuery(TaxCalculation.FIND_BY_YEAR_AND_MONTH, TaxCalculation.class);

        byMonthAndYear.setParameter("year", year);
        byMonthAndYear.setParameter("month", month);

        return byMonthAndYear.getResultList();
    }

    public void deleteAll() {
        CriteriaBuilder criteriaBuilder = entityManagerFactory.getCriteriaBuilder();
        CriteriaDelete<TaxCalculation> criteriaDelete = criteriaBuilder.createCriteriaDelete(TaxCalculation.class);

        criteriaDelete.from(TaxCalculation.class);

        entityManager.createQuery(criteriaDelete).executeUpdate();
    }

    public List<TaxCalculation> findByEmployee(Employee employee) {
        TypedQuery<TaxCalculation> byEmployee = entityManager.createNamedQuery(TaxCalculation.FIND_BY_EMPLOYEE, TaxCalculation.class);

        byEmployee.setParameter("employeeId", employee.getId());

        return byEmployee.getResultList();
    }
}
