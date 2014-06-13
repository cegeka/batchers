package be.cegeka.batchers.taxcalculator.batch.service;

import be.cegeka.batchers.taxcalculator.application.domain.generation.EmployeeGeneratorCleaner;
import be.cegeka.batchers.taxcalculator.batch.domain.PayCheckRepository;
import be.cegeka.batchers.taxcalculator.batch.domain.TaxCalculationRepository;
import be.cegeka.batchers.taxcalculator.batch.domain.TaxWebserviceCallResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
@Order(value=1)
public class SpringBatchAndBatchRepositoryCleaner implements EmployeeGeneratorCleaner {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private TaxCalculationRepository taxCalculationRepository;
    @Autowired
    private TaxWebserviceCallResultRepository taxWebserviceCallResultRepository;
    @Autowired
    private PayCheckRepository payCheckRepository;

    @Override
    public void deleteAll() {
        deleteEverythingFromRepositories();
        removeJobExecutions();
    }

    private void deleteEverythingFromRepositories() {
        payCheckRepository.deleteAll();
        taxWebserviceCallResultRepository.deleteAll();
        taxCalculationRepository.deleteAll();
    }

    private void removeJobExecutions() {
        entityManager.createNativeQuery("delete from BATCH_STEP_EXECUTION_CONTEXT").executeUpdate();
        entityManager.createNativeQuery("delete from BATCH_STEP_EXECUTION").executeUpdate();
        entityManager.createNativeQuery("delete from BATCH_JOB_EXECUTION_CONTEXT").executeUpdate();
        entityManager.createNativeQuery("delete from BATCH_JOB_EXECUTION_PARAMS").executeUpdate();
        entityManager.createNativeQuery("delete from BATCH_JOB_EXECUTION").executeUpdate();
        entityManager.createNativeQuery("delete from BATCH_JOB_INSTANCE").executeUpdate();
        entityManager.flush();
    }


}
