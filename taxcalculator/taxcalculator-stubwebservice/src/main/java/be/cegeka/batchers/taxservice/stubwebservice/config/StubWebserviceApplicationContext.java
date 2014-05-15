package be.cegeka.batchers.taxservice.stubwebservice.config;

import be.cegeka.batchers.taxcalculator.infrastructure.config.PropertyPlaceHolderConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "be.cegeka.batchers.taxservice")
@Import(PropertyPlaceHolderConfig.class)
public class StubWebserviceApplicationContext {
}
