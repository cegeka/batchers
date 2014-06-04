package be.cegeka.batchers.taxcalculator.application.repositories;

import be.cegeka.batchers.taxcalculator.application.domain.AbstractRepository;
import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.to.EmployeeTo;
import org.joda.money.Money;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.util.List;

@Repository
@Transactional(readOnly = true, isolation = Isolation.DEFAULT)
public class EmployeeRepository extends AbstractRepository<Employee> {

    public List<Employee> getEmployees(int page, int pageSize) {
        TypedQuery<Employee> employees = entityManager.createNamedQuery(Employee.GET_ALL, Employee.class);
        employees.setFirstResult(page * pageSize);
        employees.setMaxResults(pageSize);

        return employees.getResultList();
    }

    public long getEmployeeCount() {
        return entityManager.createNamedQuery(Employee.GET_EMPLOYEE_COUNT, Long.class).getSingleResult();
    }

}
