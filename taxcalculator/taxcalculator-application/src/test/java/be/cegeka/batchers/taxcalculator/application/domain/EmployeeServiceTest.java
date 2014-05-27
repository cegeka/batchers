package be.cegeka.batchers.taxcalculator.application.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class EmployeeServiceTest {

    @InjectMocks
    EmployeeService employeeService = new EmployeeService();

    @Mock
    private EmployeeRepository employeeRepositoryMock;

    @Test
    public void testGetFirst20Employees() throws Exception {
        employeeService.getFirst20();

        verify(employeeRepositoryMock).getFirst20();
    }


}
