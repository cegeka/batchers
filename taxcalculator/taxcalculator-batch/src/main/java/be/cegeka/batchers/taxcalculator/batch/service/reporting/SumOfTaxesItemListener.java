package be.cegeka.batchers.taxcalculator.batch.service.reporting;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Component;

@Component
public class SumOfTaxesItemListener implements ItemProcessListener<Employee, Employee>, SkipListener<Employee, Employee> {

    @Override
    public void beforeProcess(Employee item) {
    }

    @Override
    public void afterProcess(Employee item, Employee result) {

    }

    @Override
    public void onProcessError(Employee item, Exception e) {
    }

    @Override
    public void onSkipInRead(Throwable t) {
    }

    @Override
    public void onSkipInWrite(Employee item, Throwable t) {
    }

    @Override
    public void onSkipInProcess(Employee item, Throwable t) {
    }
}