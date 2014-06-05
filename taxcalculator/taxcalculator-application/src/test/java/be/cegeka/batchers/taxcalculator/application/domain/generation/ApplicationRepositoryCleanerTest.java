package be.cegeka.batchers.taxcalculator.application.domain.generation;

import be.cegeka.batchers.taxcalculator.application.domain.EmployeeRepository;
import be.cegeka.batchers.taxcalculator.application.domain.MonthlyTaxForEmployeeRepository;
import be.cegeka.batchers.taxcalculator.application.domain.reporting.MonthlyReportRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.inOrder;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationRepositoryCleanerTest {

    @InjectMocks
    private ApplicationRepositoryCleaner applicationRepositoryCleaner;

    @Mock
    private EmployeeRepository employeeRepositoryMock;
    @Mock
    private MonthlyReportRepository monthlyReportRepositoryMock;
    @Mock
    private MonthlyTaxForEmployeeRepository monthlyTaxForEmployeeRepositoryMock;

    @Test
    public void testDeleteAll() throws Exception {
        InOrder inOrder = inOrder(monthlyTaxForEmployeeRepositoryMock, monthlyReportRepositoryMock, employeeRepositoryMock);

        applicationRepositoryCleaner.deleteAll();

        inOrder.verify(monthlyTaxForEmployeeRepositoryMock).deleteAll();
        inOrder.verify(monthlyReportRepositoryMock).deleteAll();
        inOrder.verify(employeeRepositoryMock).deleteAll();
    }
}
