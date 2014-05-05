package be.cegeka.batchers.taxcalculator.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan("be.cegeka.batchers.taxcalculator.rest")
@Import(ApplicationContext.class)
public class WebAppContext {
}
