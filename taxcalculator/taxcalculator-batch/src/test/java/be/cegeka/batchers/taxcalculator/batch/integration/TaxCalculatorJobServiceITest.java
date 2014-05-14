package be.cegeka.batchers.taxcalculator.batch.integration;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.domain.EmployeeBuilder;
import be.cegeka.batchers.taxcalculator.application.domain.EmployeeRepository;
import be.cegeka.batchers.taxcalculator.batch.service.TaxCalculatorJobService;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class TaxCalculatorJobServiceITest extends AbstractIntegrationTest {
    @Autowired
    TaxCalculatorJobService taxCalculatorJobService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @Autowired
    private String taxServiceUrl;
    @Autowired
    private String resetUrl;

    @Before
    public void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @After
    public void tearDown() {
        employeeRepository.deleteAll();
    }

    @Test
    public void testJobService() {
        mockServer.expect(requestTo(resetUrl)).andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess());
        mockServer.expect(requestTo(taxServiceUrl)).andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{ \"status\": \"OK\" }", MediaType.APPLICATION_JSON));

        Employee employee = new EmployeeBuilder()
                .withFirstName("Monica")
                .withIncome(1000)
                .build();

        employeeRepository.save(employee);
        taxCalculatorJobService.runTaxCalculatorJob();

        Employee reloadedEmployee = employeeRepository.getBy(employee.getId());
        assertThat(reloadedEmployee.getTaxTotal()).isEqualTo(Money.of(CurrencyUnit.EUR, 100));
        assertThat(reloadedEmployee.getCalculationDate()).isEqualTo(DateTime.now());
    }
}
