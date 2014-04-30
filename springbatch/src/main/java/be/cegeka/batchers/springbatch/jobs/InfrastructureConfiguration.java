package be.cegeka.batchers.springbatch.jobs;

import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

public interface InfrastructureConfiguration {

	@Bean
	public abstract DataSource dataSource();

}