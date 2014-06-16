package be.cegeka.batchers.taxcalculator.presentation.config;

import be.cegeka.batchers.taxcalculator.application.config.EmployeeGeneratorConfig;
import be.cegeka.batchers.taxcalculator.application.config.WebserviceCallConfig;
import be.cegeka.batchers.taxcalculator.infrastructure.config.InfrastructureConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@Import({InfrastructureConfig.class, EmployeeGeneratorConfig.class, WebserviceCallConfig.class})
@ComponentScan(basePackages = {"be.cegeka.batchers.taxcalculator.batch.config", "be.cegeka.batchers.taxcalculator.presentation"})
public class WebAppContext {
}
