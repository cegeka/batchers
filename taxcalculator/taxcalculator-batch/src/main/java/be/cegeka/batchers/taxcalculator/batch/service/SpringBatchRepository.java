package be.cegeka.batchers.taxcalculator.batch.service;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class SpringBatchRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void removeJobExecutions() {
        entityManager.createNativeQuery("delete from BATCH_STEP_EXECUTION_CONTEXT").executeUpdate();
        entityManager.createNativeQuery("delete from BATCH_STEP_EXECUTION").executeUpdate();
        entityManager.createNativeQuery("delete from BATCH_JOB_EXECUTION_CONTEXT").executeUpdate();
        entityManager.createNativeQuery("delete from BATCH_JOB_EXECUTION_PARAMS").executeUpdate();
        entityManager.createNativeQuery("delete from BATCH_JOB_EXECUTION").executeUpdate();
        entityManager.createNativeQuery("delete from BATCH_JOB_INSTANCE").executeUpdate();
        entityManager.flush();
    }
}
