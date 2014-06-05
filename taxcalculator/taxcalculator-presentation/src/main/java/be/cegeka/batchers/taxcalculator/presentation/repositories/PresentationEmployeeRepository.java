package be.cegeka.batchers.taxcalculator.presentation.repositories;

import be.cegeka.batchers.taxcalculator.to.EmployeeTo;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
@Transactional(readOnly = true, isolation = Isolation.DEFAULT)
public class PresentationEmployeeRepository {

    public static final String GET_EMPLOYEE_COUNT_QUERY = "SELECT COUNT(e) FROM Employee e";

    public static final String GET_EMPLOYEE_DETAIL_QUERY = "SELECT NEW be.cegeka.batchers.taxcalculator.to.EmployeeTo(e.firstName, e.lastName, e.email, e.income, " +
            "(select sum(mtfe.tax) from MonthlyTaxForEmployee mtfe where mtfe.employee.id = e.id), e.id) " +
            "FROM Employee e WHERE e.id = :id";

    public static final String GET_EMPLOYEES_TOTAL_TAX_QUERY = "SELECT NEW be.cegeka.batchers.taxcalculator.to.EmployeeTo(e.firstName, e.lastName, e.email, e.income, " +
            "(select sum(mtfe.tax) from MonthlyTaxForEmployee mtfe where mtfe.employee.id = e.id), e.id) " +
            "FROM Employee e ORDER BY e.id";

    @PersistenceContext
    protected EntityManager entityManager;

    public EmployeeTo getEmployee(long id) {
        TypedQuery<EmployeeTo> employees = entityManager.createQuery(GET_EMPLOYEE_DETAIL_QUERY, EmployeeTo.class);
        employees.setParameter("id", id);
        return employees.getSingleResult();
    }

    public List<EmployeeTo> getEmployees(int page, int pageSize) {
        TypedQuery<EmployeeTo> employees = entityManager.createQuery(GET_EMPLOYEES_TOTAL_TAX_QUERY, EmployeeTo.class);

        employees.setFirstResult(page * pageSize);
        employees.setMaxResults(pageSize);

        return employees.getResultList();
    }

    public long getEmployeeCount() {
        return entityManager.createQuery(GET_EMPLOYEE_COUNT_QUERY, Long.class).getSingleResult();
    }
}
