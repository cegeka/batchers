package be.cegeka.batchers.taxservice.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Created by monicat on 29/04/2014.
 */

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "be.cegeka")
public class AppConfig {
}
