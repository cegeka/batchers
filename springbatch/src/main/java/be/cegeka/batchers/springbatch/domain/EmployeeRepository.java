package be.cegeka.batchers.springbatch.domain;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

@Repository
@Transactional(isolation = Isolation.DEFAULT)
public class EmployeeRepository {
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    public void save(Employee employee) {
        entityManager.persist(employee);
    }

    public Employee getBy(Long id) {
        return entityManager.find(Employee.class, id);
    }

    public Long count() {

        CriteriaBuilder criteriaBuilder = entityManagerFactory.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        criteriaQuery.select(
                criteriaBuilder.count(
                        criteriaQuery.from(Employee.class))
        );

         return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    public List<Employee> getAll() {
        CriteriaBuilder criteriaBuilder = entityManagerFactory.getCriteriaBuilder();
        CriteriaQuery<Employee> criteriaQuery = criteriaBuilder.createQuery(Employee.class);

        criteriaQuery.select(criteriaQuery.from(Employee.class));

        return entityManager.createQuery(criteriaQuery).getResultList();

    }
}
