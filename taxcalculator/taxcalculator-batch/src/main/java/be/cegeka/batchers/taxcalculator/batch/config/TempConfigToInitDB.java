package be.cegeka.batchers.taxcalculator.batch.config;

import be.cegeka.batchers.taxcalculator.infrastructure.config.PersistenceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

@Configuration
@Profile("!remotePartitioningSlave")
public class TempConfigToInitDB {

    @Autowired
    private PersistenceConfig persistenceConfig;

    @Value(value = "${drop.script:org/springframework/batch/core/schema-drop-hsqldb.sql}")
    private String dropScript;
    @Value(value = "${schema.script:org/springframework/batch/core/schema-hsqldb.sql}")
    private String schemaScript;

    @Bean
    public DataSourceInitializer dataSourceInitializer() {
        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(persistenceConfig.dataSource());
        dataSourceInitializer.setDatabasePopulator(dataSourcePopulator());
        return dataSourceInitializer;
    }

    private DatabasePopulator dataSourcePopulator() {
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        databasePopulator.setScripts(
                new ClassPathResource(dropScript),
                new ClassPathResource(schemaScript)
        );
        return databasePopulator;
    }
}
