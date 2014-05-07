package be.cegeka.batchers.taxcalculator.rest;

import be.cegeka.batchers.taxcalculator.domain.Employee;
import be.cegeka.batchers.taxcalculator.domain.EmployeeBuilder;
import be.cegeka.batchers.taxcalculator.domain.EmployeeService;
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

import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
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
        Employee employee = new EmployeeBuilder()
                .withIncome(200)
                .withFirstName("firstName")
                .build();
        String expectedJSON = new Jackson2JsonObjectMapper().toJson(asList(employee));

        when(employeeServiceMock.getFirst20()).thenReturn(asList(employee));

        MvcResult mvcResult = mockMvc.perform(get("/employees").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String actualJSON = mvcResult.getResponse().getContentAsString();

        assertThat(actualJSON).isEqualTo(expectedJSON);
    }
}

