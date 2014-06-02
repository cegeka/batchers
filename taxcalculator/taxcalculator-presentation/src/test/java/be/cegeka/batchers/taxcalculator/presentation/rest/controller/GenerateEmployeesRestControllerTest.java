package be.cegeka.batchers.taxcalculator.presentation.rest.controller;

import be.cegeka.batchers.taxcalculator.application.domain.EmployeeGeneratorService;
import be.cegeka.batchers.taxcalculator.application.domain.EmployeeService;
import be.cegeka.batchers.taxcalculator.batch.service.SpringBatchRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class GenerateEmployeesRestControllerTest {

    public static final long TEST_NUMBER_OF_EMPLOYEES_TO_GENERATE = 4L;
    private MockMvc mockMvc;

    @InjectMocks
    private GenerateEmployeesRestController generateEmployeesRestController;

    @Mock
    private EmployeeGeneratorService employeeGeneratorServiceMock;

    @Mock
    private EmployeeService employeeServiceMock;

    @Mock
    private SpringBatchRepository springBatchRepositoryMock;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(generateEmployeesRestController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    @Test
    public void testGetFirst20Employees() throws Exception {

        mockMvc.perform(post("/generateEmployees").param("employeesCount", "4"))
                .andExpect(status().isOk())
                .andReturn();

        verify(employeeGeneratorServiceMock).resetEmployees(TEST_NUMBER_OF_EMPLOYEES_TO_GENERATE);
    }

    @Test
    public void whenResetEmployees_thenBatchRepositoryIsCalled() throws Exception {
        mockMvc.perform(post("/generateEmployees").param("employeesCount", "4"))
                .andExpect(status().isOk())
                .andReturn();

        verify(springBatchRepositoryMock).removeJobExecutions();
        verifyNoMoreInteractions(springBatchRepositoryMock);
    }
}
