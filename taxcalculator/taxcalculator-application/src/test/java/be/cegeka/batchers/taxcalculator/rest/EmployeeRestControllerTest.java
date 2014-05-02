package be.cegeka.batchers.taxcalculator.rest;

import be.cegeka.batchers.taxcalculator.domain.Employee;
import be.cegeka.batchers.taxcalculator.domain.EmployeeService;
import org.fest.assertions.api.Assertions;
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
import org.springframework.integration.support.json.JacksonJsonObjectMapper;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

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
        List<Employee> employees = Lists.newArrayList(employee);
        String expectedJSON = new JacksonJsonObjectMapper().toJson(employees);

        Mockito.when(employeeServiceMock.getFirst20()).thenReturn(employees);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/employees").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        String actualJSON = mvcResult.getResponse().getContentAsString();

        Assertions.assertThat(actualJSON).isEqualTo(expectedJSON);
    }
}

