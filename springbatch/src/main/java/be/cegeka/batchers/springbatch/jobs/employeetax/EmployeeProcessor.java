package be.cegeka.batchers.springbatch.jobs.employeetax;

import be.cegeka.batchers.springbatch.domain.Employee;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.springframework.batch.item.ItemProcessor;

/**
 * Created by andreip on 29.04.2014.
 */
public class EmployeeProcessor implements ItemProcessor<Employee, Employee> {
    @Override
    public Employee process(Employee item) {
        DateTime calculationDate = item.getCalculationDate();
        if (!taxWasCalculatedThisMonth(calculationDate)) {
            int tax = (int) (item.getIncome() * 0.1);
            int previousTax = calculationDate == null ? 0 : item.getTaxTotal();
            item.setTaxTotal(previousTax + tax);
        }
        return item;
    }

    private boolean taxWasCalculatedThisMonth(DateTime calculationDate) {
        return calculationDate != null && getCurrentMonthInterval().contains(calculationDate);
    }

    private Interval getCurrentMonthInterval() {
        return DateTime.now().monthOfYear().toInterval();
    }
}
