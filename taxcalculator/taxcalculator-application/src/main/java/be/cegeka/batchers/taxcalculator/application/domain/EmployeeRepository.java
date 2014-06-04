package be.cegeka.batchers.taxcalculator.application.domain;

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

    public List<Long> getEmployeeIds(long year, long month, long jobExecutionId) {
        TypedQuery<Long> namedQuery = entityManager
                .createNamedQuery(Employee.GET_UNPROCESSED_EMPLOYEES_IDS_BY_YEAR_AND_MONTH, Long.class);
        namedQuery.setParameter("year", (int) year);
        namedQuery.setParameter("month", (int) month);
        namedQuery.setParameter("jobExecutionId", jobExecutionId);

        return namedQuery.getResultList();
    }
}
