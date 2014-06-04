package be.cegeka.batchers.taxcalculator.presentation.rest.controller;

import be.cegeka.batchers.taxcalculator.application.domain.*;
import be.cegeka.batchers.taxcalculator.presentation.rest.model.EmployeeTaxTo;
import be.cegeka.batchers.taxcalculator.to.EmployeeTo;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.integration.support.json.Jackson2JsonObjectMapper;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static java.util.Arrays.asList;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(MockitoJUnitRunner.class)
public class EmployeeRestControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private EmployeeRestController employeeRestController;

    @Mock
    private MonthlyTaxForEmployeeRepository monthlyTaxForEmployeeRepository;

    @Mock
    private EmployeeService employeeServiceMock;

    @Mock
    private TaxCalculationRepository taxCalculationRepositoryMock;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(employeeRestController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    @Test
    public void givenOneEmployee_whenGetEmployees_thenReturnCorrectJson() throws Exception {
        Employee employee = new EmployeeTestBuilder()
                .withIncome(200)
                .withFirstName("firstName")
                .build();

        EmployeeTo employeeTo = new EmployeeTo(employee.getFirstName(), employee.getLastName(), employee.getEmail(), employee.getIncome(), Money.parse("EUR 200"), 1L);
        String expectedJSON = new Jackson2JsonObjectMapper().toJson(asList(employeeTo));
        when(employeeServiceMock.getEmployees(0, 10)).thenReturn(asList(employeeTo));

        MvcResult mvcResult = mockMvc.perform(get("/employees?page=0&pageSize=10").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String actualJSON = mvcResult.getResponse().getContentAsString();

        assertThat(actualJSON).isEqualTo(expectedJSON);
    }

    @Test
    public void givenOneEmployee_whenGetEmployeeCount_thenReturnCorrectJson() throws Exception {
        when(employeeServiceMock.getEmployeeCount()).thenReturn(1L);

        MvcResult mvcResult = mockMvc.perform(get("/employees/count").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String actualResult = mvcResult.getResponse().getContentAsString();

        assertThat(actualResult).isEqualTo("1");
    }

    @Test
    public void testGetEmployeeTaxes() throws Exception {
        long employeeId = 12L;
        int year = 2012;
        int month = 2;
        Money tax = Money.of(CurrencyUnit.EUR, 10);

        Employee employee = new Employee();
        TaxCalculation taxCalculation = TaxCalculation.from(1L, employee, year, month, tax);
        when(employeeServiceMock.getEmployee(employeeId)).thenReturn(employee);
        when(employeeServiceMock.getEmployeeTaxes(employeeId)).thenReturn(asList(taxCalculation));

        List<EmployeeTaxTo> employeeTaxes = employeeRestController.getEmployeeTaxes(employeeId);

        assertThat(employeeTaxes).hasSize(1);
        assertThat(employeeTaxes.get(0).getStatus()).isEqualTo("IN PROGRESS");
        assertThat(employeeTaxes.get(0).getYear()).isEqualTo(year);
        assertThat(employeeTaxes.get(0).getMonth()).isEqualTo(month);
        assertThat(employeeTaxes.get(0).getTax()).isEqualTo(tax);

        verify(employeeServiceMock).getEmployee(employeeId);
        verify(employeeServiceMock).getEmployeeTaxes(employeeId);
        verify(monthlyTaxForEmployeeRepository).find(employee, year, month);
    }
}

