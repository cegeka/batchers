package be.cegeka.batchers.taxcalculator.application.service;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.repositories.EmployeeRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EmployeeGeneratorServiceTest {

    private static final Long TEST_MAX_GENERATED_EMPLOYEES = 300001L;
    private static final long TEST_ZERO_EMPLOYEES = 0L;

    @InjectMocks
    private EmployeeGeneratorService employeeGeneratorService;

    @Mock
    private EmployeeRepository employeeRepositoryMock;

    @Mock
    private EmployeeService employeeServiceMock;

    @Test(expected = IllegalArgumentException.class)
    public void whenGenerateTooManyEmployees_thenThrowException() {
        employeeGeneratorService.resetEmployees(TEST_MAX_GENERATED_EMPLOYEES);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenGenerateNoEmployees_thenThrowException() {
        employeeGeneratorService.resetEmployees(TEST_ZERO_EMPLOYEES);
    }

    @Test
    public void whenGenerateEmployees_thenEmployeesGetSaved() {
        employeeGeneratorService.resetEmployees(3L);

        verify(employeeServiceMock).deleteAll();
        verify(employeeRepositoryMock, times(3)).save(any(Employee.class));
    }


}
