package be.cegeka.batchers.taxcalculator.application.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class EmployeeServiceTest {
    public static final long EMPLOYEE_ID = 123L;

    @InjectMocks
    private EmployeeService employeeService = new EmployeeService();

    @Mock
    private EmployeeRepository employeeRepositoryMock;
    @Mock
    private TaxCalculationRepository taxCalculationRepository;

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

    @Test
    public void givenEmployees_whenGetEmployeeIds_thenRepositoryIsCalledWithCorrectParameters() {
        employeeService.getEmployeeIds(2014, 5, 1);
        verify(employeeRepositoryMock).getEmployeeIds(2014, 5, 1);
        verifyNoMoreInteractions(employeeRepositoryMock);
    }

    @Test
    public void testGetEmployee() throws Exception {
        employeeService.getEmployee(EMPLOYEE_ID);
        verify(employeeRepositoryMock).getBy(EMPLOYEE_ID);
        verifyNoMoreInteractions(employeeRepositoryMock);
    }

    @Test
    public void testGetEmployeeTaxes() throws Exception {
        Employee employee = new Employee();
        when(employeeService.getEmployee(EMPLOYEE_ID)).thenReturn(employee);

        employeeService.getEmployeeTaxes(EMPLOYEE_ID);

        verify(employeeRepositoryMock).getBy(EMPLOYEE_ID);
        verify(taxCalculationRepository).findByEmployee(employee);
    }
}
