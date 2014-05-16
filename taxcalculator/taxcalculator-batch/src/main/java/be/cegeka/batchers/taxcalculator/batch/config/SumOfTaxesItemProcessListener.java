package be.cegeka.batchers.taxcalculator.batch.config;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SumOfTaxesItemProcessListener implements ItemProcessListener<Employee, Employee> {
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
}