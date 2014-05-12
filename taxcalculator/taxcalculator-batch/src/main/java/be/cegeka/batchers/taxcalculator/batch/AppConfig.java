package be.cegeka.batchers.taxcalculator.batch;

import be.cegeka.batchers.taxcalculator.batch.api.JobStartListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Configuration
@Import(PropertyPlaceHolderConfig.class)
public class AppConfig {

    @Value(value = "${taxservice.url:/taxservice}")
    String taxServiceUrl;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(Arrays.asList(
                new MappingJackson2HttpMessageConverter(),
                new StringHttpMessageConverter()
        ));
        return restTemplate;
    }

    @Bean
    public String taxServiceUrl(){
        return taxServiceUrl;
    }

    @Bean
    public JobStartListener defaultJobStartListener() {
        return new JobStartListener() {
            @Override
            public void jobHasBeenStarted(String jobName) {

            }
        };
    }
}
