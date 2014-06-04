package be.cegeka.batchers.taxcalculator.application.domain;

import be.cegeka.batchers.taxcalculator.to.EmployeeTo;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.util.List;

@Repository
@Transactional(readOnly = true, isolation = Isolation.DEFAULT)
public class EmployeeRepository extends AbstractRepository<Employee> {

    public List<EmployeeTo> getEmployees(int page, int pageSize) {
        TypedQuery<EmployeeTo> employees = entityManager.createNamedQuery(Employee.GET_EMPLOYEES_TOTAL_TAX_NAME, EmployeeTo.class);
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
        namedQuery.setParameter("year", year);
        namedQuery.setParameter("month", month);
        namedQuery.setParameter("jobExecutionId", jobExecutionId);

        return namedQuery.getResultList();
    }
}
