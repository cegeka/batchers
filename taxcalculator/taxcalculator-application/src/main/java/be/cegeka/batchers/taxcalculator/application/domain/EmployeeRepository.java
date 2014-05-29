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

    public List<EmployeeTo> getFirst20() {
        TypedQuery<EmployeeTo> first20 = entityManager.createNamedQuery(Employee.GET_EMPLOYEES_TOTAL_TAX_NAME, EmployeeTo.class);
        first20.setMaxResults(20);

        return first20.getResultList();
    }

}
