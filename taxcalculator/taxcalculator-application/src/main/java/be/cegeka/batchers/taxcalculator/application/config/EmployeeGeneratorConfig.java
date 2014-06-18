package be.cegeka.batchers.taxcalculator.application.config;

import be.cegeka.batchers.taxcalculator.application.ApplicationInitializer;
import be.cegeka.batchers.taxcalculator.application.domain.generation.EmployeeGenerator;
import be.cegeka.batchers.taxcalculator.infrastructure.config.PersistenceConfig;
import be.cegeka.batchers.taxcalculator.infrastructure.config.PropertyPlaceHolderConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@Configuration
@EnableTransactionManagement
@Import({PersistenceConfig.class, PropertyPlaceHolderConfig.class})
@ComponentScan(basePackages = "be.cegeka.batchers.taxcalculator.application")
@PropertySource("classpath:taxcalculator-application.properties")
public class EmployeeGeneratorConfig {
    public static final String JNDI_SMTP_USERNAME = "java:comp/env/smtp_username";
    public static final String JNDI_SMTP_PASSWORD = "java:comp/env/smtp_password";
    public static final String JNDI_SMTP_PORT = "java:comp/env/smtp_port";
    public static final String JNDI_SMTP_SERVER = "java:comp/env/smtp_server";

    private static Logger LOG = LoggerFactory.getLogger(EmployeeGeneratorConfig.class);

    @Autowired
    protected EmployeeGenerator employeeGenerator;

    @Bean
    @Profile("!remotePartitioningSlave")
    public ApplicationInitializer applicationInitializer() {
        ApplicationInitializer applicationInitializer = new ApplicationInitializer();
        applicationInitializer.setGenerateEmployees(true);
        applicationInitializer.setEmployeeGenerator(employeeGenerator);
        return applicationInitializer;
    }

    @Bean
    public String smtp_username() {
        return lookupValue(JNDI_SMTP_USERNAME);
    }

    @Bean
    public String smtp_password() {
        return lookupValue(JNDI_SMTP_PASSWORD);
    }

    @Bean
    public String smtp_port() {
        return lookupValue(JNDI_SMTP_PORT);
    }

    @Bean
    public String smtp_server() {
        return lookupValue(JNDI_SMTP_SERVER);
    }

    private String lookupValue(String key) {
        try {
            Context context = new InitialContext();
            return (String) context.lookup(key);
        } catch (NamingException e) {
            LOG.error("Value for {} is not defined in context.xml of your server", key);
        }
        return null;
    }

}
