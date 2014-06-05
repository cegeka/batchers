package be.cegeka.batchers.taxcalculator.application.domain;

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
}
