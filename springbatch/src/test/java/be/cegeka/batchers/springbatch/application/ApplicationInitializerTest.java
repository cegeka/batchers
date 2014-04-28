package be.cegeka.batchers.springbatch.application;

import be.cegeka.batchers.springbatch.domain.EmployeeGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.event.ContextRefreshedEvent;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationInitializerTest {

    @Mock
    private EmployeeGenerator employeeGeneratorMock;

    @Mock
    private ContextRefreshedEvent contextRefreshedEventMock;

    private ApplicationInitializer applicationInitializer;

    @Before
    public void setUp() throws Exception {
        applicationInitializer = new ApplicationInitializer();
        applicationInitializer.setEmployeeGenerator(employeeGeneratorMock);
    }

    @Test
    public void testOnApplicationEventWhenGenerateIsTrue() throws Exception {
        applicationInitializer.setGenerateEmployees(true);

        applicationInitializer.onApplicationEvent(contextRefreshedEventMock);

        verify(employeeGeneratorMock).generateAll();
    }
    @Test
    public void testOnApplicationEventWhenGenerateIsFalse() throws Exception {
        applicationInitializer.setGenerateEmployees(false);

        applicationInitializer.onApplicationEvent(contextRefreshedEventMock);

        verifyZeroInteractions(employeeGeneratorMock);
    }
}
