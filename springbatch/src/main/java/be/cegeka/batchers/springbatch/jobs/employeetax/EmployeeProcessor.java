package be.cegeka.batchers.springbatch.jobs.employeetax;

import be.cegeka.batchers.springbatch.domain.Employee;
import org.springframework.batch.item.ItemProcessor;

/**
 * Created by andreip on 29.04.2014.
 */
public class EmployeeProcessor implements ItemProcessor<Employee, Employee> {
    @Override
    public Employee process(Employee item) throws Exception {
        int tax = (int) (item.getIncome() * 0.1);
        int previousTax = item.getCalculationDate()==null?0:item.getTaxTotal();
        item.setTaxTotal(previousTax + tax);
        return item;
    }
}
