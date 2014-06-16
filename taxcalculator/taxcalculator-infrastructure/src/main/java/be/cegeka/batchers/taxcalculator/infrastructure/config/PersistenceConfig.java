package be.cegeka.batchers.taxcalculator.infrastructure.config;

import org.apache.commons.dbcp.BasicDataSource;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

import static be.cegeka.batchers.taxcalculator.infrastructure.config.Environment.getCurrentEnvironment;


@Configuration
@Import(PropertyPlaceHolderConfig.class)
@PropertySource({
        "classpath:conf/taxcalculator-infrastructure.${APP_ENV}.properties"
})
public class PersistenceConfig {

    @Value("${jdbc.driver}")
    private String driverClassName;

    @Value("${jdbc.url}")
    private String url;

    @Value("${jdbc.user}")
    private String user;

    @Value("${jdbc.password}")
    private String password;

    @Value("${hibernate.dialect:org.hibernate.dialect.HSQLDialect}")
    private String hibernateDialect = "org.hibernate.dialect.HSQLDialect";

    private JpaTransactionManager platformTransactionManager;

    @Bean
    public DataSource dataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName(driverClassName);
        ds.setUrl(url);
        ds.setUsername(user);
        ds.setPassword(password);
        ds.setDefaultAutoCommit(false);
        return ds;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setDatabasePlatform(hibernateDialect);
        return jpaVendorAdapter;
    }

    @Bean
    public EntityManagerFactory entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setDataSource(dataSource());
        entityManagerFactory.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        entityManagerFactory.setJpaVendorAdapter(jpaVendorAdapter());
        entityManagerFactory.setPackagesToScan("be.cegeka.batchers.taxcalculator");

        Properties properties = new Properties();
        //properties.put("hibernate.show_sql", "true");
        if (getCurrentEnvironment().isMaster()) {
            properties.put("hibernate.hbm2ddl.auto", "create-drop");
        }

        entityManagerFactory.setJpaProperties(properties);
        entityManagerFactory.afterPropertiesSet();

        return entityManagerFactory.getNativeEntityManagerFactory();
    }

    @Bean
    public JpaTransactionManager transactionManager() {
        if (platformTransactionManager == null) {
            platformTransactionManager = new JpaTransactionManager(entityManagerFactory());
        }
        return platformTransactionManager;

    }
}
