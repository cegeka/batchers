package be.cegeka.batchers.taxservice.stubwebservice.config;

import be.cegeka.batchers.taxcalculator.application.infrastructure.TaxCalculatorSpringJUnitClassRunner;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import javax.annotation.Resource;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(TaxCalculatorSpringJUnitClassRunner.class)
@ActiveProfiles("test")
@WebAppConfiguration
@ContextConfiguration(classes = StubWebserviceConfig.class)
public abstract class WebAppTestConfig {

    @Resource
    protected org.springframework.web.context.WebApplicationContext wac;
    protected MockMvc mockMvc;

    @Before
    public void before() {
        this.mockMvc = webAppContextSetup(this.wac).build();
    }

}
