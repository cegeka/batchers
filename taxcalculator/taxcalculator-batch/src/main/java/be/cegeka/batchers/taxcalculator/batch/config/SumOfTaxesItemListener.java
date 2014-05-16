package be.cegeka.batchers.taxcalculator.batch.config;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.SkipListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SumOfTaxesItemListener implements ItemProcessListener<Employee, Employee>, SkipListener<Employee, Employee> {
    @Autowired
    private SumOfTaxes sumOfTaxes;

    @Override
    public void beforeProcess(Employee item) {
    }

    @Override
    public void afterProcess(Employee item, Employee result) {
        sumOfTaxes.addToSuccessSum(result.getIncomeTax());
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
        sumOfTaxes.addToFailingSum(item.getIncomeTax());
    }
}