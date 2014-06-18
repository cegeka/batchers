package be.cegeka.batchers.taxcalculator.presentation.config;

import be.cegeka.batchers.taxcalculator.presentation.repositories.PresentationEmployeeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Configuration
public class PresentationTestConfig {

  @PersistenceContext
  private EntityManager entityManager;

  @Bean
  public PresentationEmployeeRepository presentationEmployeeRepository(){
    PresentationEmployeeRepository presentationEmployeeRepository = new PresentationEmployeeRepository();
    presentationEmployeeRepository.setEntityManager(entityManager);
    return presentationEmployeeRepository;
  }
}
