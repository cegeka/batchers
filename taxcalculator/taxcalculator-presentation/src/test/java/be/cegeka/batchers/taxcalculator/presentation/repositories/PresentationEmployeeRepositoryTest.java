package be.cegeka.batchers.taxcalculator.presentation.repositories;

import be.cegeka.batchers.taxcalculator.application.config.EmployeeGeneratorTestConfig;
import be.cegeka.batchers.taxcalculator.application.config.WebserviceCallConfig;
import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.domain.EmployeeTestBuilder;
import be.cegeka.batchers.taxcalculator.application.domain.MonthlyTaxForEmployee;
import be.cegeka.batchers.taxcalculator.application.domain.MonthlyTaxForEmployeeTestBuilder;
import be.cegeka.batchers.taxcalculator.batch.config.EmployeeJobTestConfig;
import be.cegeka.batchers.taxcalculator.batch.config.singlejvm.EmployeeJobConfigSingleJvm;
import be.cegeka.batchers.taxcalculator.batch.integration.AbstractIntegrationTest;
import be.cegeka.batchers.taxcalculator.infrastructure.config.InfrastructureConfig;
import be.cegeka.batchers.taxcalculator.infrastructure.config.PersistenceConfig;
import be.cegeka.batchers.taxcalculator.infrastructure.config.PropertyPlaceHolderConfig;
import be.cegeka.batchers.taxcalculator.presentation.config.PresentationTestConfig;
import be.cegeka.batchers.taxcalculator.presentation.to.EmployeeTo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.fest.assertions.api.Assertions.assertThat;

@ContextConfiguration(classes = {EmployeeJobTestConfig.class, EmployeeJobConfigSingleJvm.class,
        EmployeeGeneratorTestConfig.class, WebserviceCallConfig.class, PropertyPlaceHolderConfig.class,
        InfrastructureConfig.class, PersistenceConfig.class, PresentationTestConfig.class})
@Transactional(readOnly = true, isolation = Isolation.DEFAULT)
public class PresentationEmployeeRepositoryTest extends AbstractIntegrationTest {

  @Autowired
  private PresentationEmployeeRepository presentationEmployeeRepository;

  @PersistenceContext
  protected EntityManager entityManager;

  @Test
  public void givenEmployeeWithError_whenGetEmployees_employeeWithErrorIsReturned() {
    Employee first = new EmployeeTestBuilder().build();
    MonthlyTaxForEmployee monthlyTax = new MonthlyTaxForEmployeeTestBuilder()
            .withEmployee(first)
            .withLastErrorMessage("BOOM!!!")
            .build();
    entityManager.persist(first);
    entityManager.persist(monthlyTax);
    entityManager.flush();

    EmployeeTo employeeTo = presentationEmployeeRepository.getEmployees(0, 1).get(0);

    assertThat(employeeTo.getErrorCount()).isEqualTo(1L);
  }

  @Test
  public void givenEmployeeWithNoError_whenGetEmployees_employeeWithNoErrorIsReturned() {
    Employee first = new EmployeeTestBuilder().build();
    MonthlyTaxForEmployee monthlyTax = new MonthlyTaxForEmployeeTestBuilder()
            .withEmployee(first)
            .build();
    entityManager.persist(first);
    entityManager.persist(monthlyTax);
    entityManager.flush();

    EmployeeTo employeeTo = presentationEmployeeRepository.getEmployees(0, 1).get(0);

    assertThat(employeeTo.getErrorCount()).isEqualTo(0L);
  }

  @Test
  public void givenEmployeeWithError_whenGetEmploye_employeeWithErrorIsReturned() {
    Employee first = new EmployeeTestBuilder().build();
    MonthlyTaxForEmployee monthlyTax = new MonthlyTaxForEmployeeTestBuilder()
            .withEmployee(first)
            .withLastErrorMessage("BOOM!!!")
            .build();
    entityManager.persist(first);
    entityManager.persist(monthlyTax);
    entityManager.flush();

    EmployeeTo employeeTo = presentationEmployeeRepository.getEmployee(first.getId());

    assertThat(employeeTo.getErrorCount()).isEqualTo(1L);
  }


  @Test
  public void givenEmployeeWithNoError_whenGetEmployee_employeeWithNoErrorIsReturned() {
    Employee first = new EmployeeTestBuilder().build();
    MonthlyTaxForEmployee monthlyTax = new MonthlyTaxForEmployeeTestBuilder()
            .withEmployee(first)
            .build();
    entityManager.persist(first);
    entityManager.persist(monthlyTax);
    entityManager.flush();

    EmployeeTo employeeTo = presentationEmployeeRepository.getEmployee(first.getId());

    assertThat(employeeTo.getErrorCount()).isEqualTo(0L);
  }


}
