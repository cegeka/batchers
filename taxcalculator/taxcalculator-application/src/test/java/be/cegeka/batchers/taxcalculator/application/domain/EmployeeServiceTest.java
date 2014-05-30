package be.cegeka.batchers.taxcalculator.application.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;


@RunWith(MockitoJUnitRunner.class)
public class EmployeeServiceTest {

    @InjectMocks
    EmployeeService employeeService = new EmployeeService();

    @Mock
    private EmployeeRepository employeeRepositoryMock;

    @Test
    public void givenEmployees_whenGetEmployees_thenRepositoryIsCalledWithCorrectParameters() throws Exception {
        employeeService.getEmployees(0, 10);

        verify(employeeRepositoryMock).getEmployees(0, 10);
        verifyNoMoreInteractions(employeeRepositoryMock);
    }

    @Test
    public void givenEmployees_whenGetEmployeeCount_thenRepositoryIsCalled() throws Exception {
        employeeService.getEmployeeCount();

        verify(employeeRepositoryMock).getEmployeeCount();
        verifyNoMoreInteractions(employeeRepositoryMock);
    }


}
