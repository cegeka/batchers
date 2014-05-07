package be.cegeka.batchers.taxcalculator.presentation.rest;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.domain.EmployeeService;
import org.fest.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.integration.support.json.Jackson2JsonObjectMapper;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(MockitoJUnitRunner.class)
public class EmployeeRestControllerTest {
    MockMvc mockMvc;

    @InjectMocks
    EmployeeRestController employeeRestController;

    @Mock
    EmployeeService employeeServiceMock;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(employeeRestController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    @Test
    public void testGetFirst20Employees() throws Exception {
        Employee employee = new Employee();
        employee.setFirstName("firstName");
        employee.setIncome(200);
        List<Employee> employees = Lists.newArrayList(employee);
        String expectedJSON = new Jackson2JsonObjectMapper().toJson(employees);

        Mockito.when(employeeServiceMock.getFirst20()).thenReturn(employees);

        MvcResult mvcResult = mockMvc.perform(get("/employees").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        String actualJSON = mvcResult.getResponse().getContentAsString();

        assertThat(actualJSON).isEqualTo(expectedJSON);
    }
}

