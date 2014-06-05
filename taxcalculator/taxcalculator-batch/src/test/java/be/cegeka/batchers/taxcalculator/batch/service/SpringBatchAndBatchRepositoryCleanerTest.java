package be.cegeka.batchers.taxcalculator.batch.service;

import be.cegeka.batchers.taxcalculator.batch.domain.PayCheckRepository;
import be.cegeka.batchers.taxcalculator.batch.domain.TaxCalculationRepository;
import be.cegeka.batchers.taxcalculator.batch.domain.TaxWebserviceCallResultRepository;
import org.hibernate.jpa.internal.QueryImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

@RunWith(MockitoJUnitRunner.class)
public class SpringBatchAndBatchRepositoryCleanerTest {

    @InjectMocks
    private SpringBatchAndBatchRepositoryCleaner cleaner;

    @Mock
    private EntityManager entityManager;
    @Mock
    private TaxCalculationRepository taxCalculationRepository;
    @Mock
    private TaxWebserviceCallResultRepository taxWebserviceCallResultRepository;
    @Mock
    private PayCheckRepository payCheckRepository;
    @Mock
    private Query query;

    @Test
    public void testDeleteAll() throws Exception {
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);

        cleaner.deleteAll();

        verify(payCheckRepository).deleteAll();
        verify(taxWebserviceCallResultRepository).deleteAll();
        verify(taxCalculationRepository).deleteAll();

        verify(entityManager, times(6)).createNativeQuery(anyString());
        verify(query, times(6)).executeUpdate();
    }
}
