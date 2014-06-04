package be.cegeka.batchers.taxcalculator.application.domain;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import java.lang.reflect.ParameterizedType;
import java.util.List;

@Transactional(readOnly = true, isolation = Isolation.DEFAULT)
public abstract class AbstractRepository<E> {

    @PersistenceContext
    protected EntityManager entityManager;
    @Autowired
    protected EntityManagerFactory entityManagerFactory;
    private Class<E> entityClass;

    @Transactional
    public E save(E entity) {
        entityManager.persist(entity);
        return entity;
    }

    public E getBy(Long id) {
        return entityManager.find(getEntityClass(), id);
    }

    public Long count() {
        CriteriaBuilder criteriaBuilder = entityManagerFactory.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        criteriaQuery.select(
                criteriaBuilder.count(
                        criteriaQuery.from(getEntityClass()))
        );

        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    public List<E> getAll() {
        CriteriaBuilder criteriaBuilder = entityManagerFactory.getCriteriaBuilder();
        CriteriaQuery<E> criteriaQuery = criteriaBuilder.createQuery(getEntityClass());

        criteriaQuery.select(criteriaQuery.from(getEntityClass()));

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public void deleteAll() {
        CriteriaBuilder criteriaBuilder = entityManagerFactory.getCriteriaBuilder();
        CriteriaDelete<E> criteriaDelete = criteriaBuilder.createCriteriaDelete(getEntityClass());

        criteriaDelete.from(getEntityClass());

        entityManager.createQuery(criteriaDelete).executeUpdate();
    }

    @Transactional
    public void truncate() {
        String tableName = getTableName();
        String sqlString = "truncate table " + tableName;
        entityManager.createNativeQuery(sqlString).executeUpdate();
        entityManager.flush();
    }

    protected String getTableName() {
        return getEntityClass().getSimpleName();
    }

    public Class<E> getEntityClass() {
        if (this.entityClass == null) {
            this.entityClass = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        }
        return entityClass;
    }

}
