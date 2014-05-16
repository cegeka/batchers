package be.cegeka.batchers.taxcalculator.batch.config;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import org.springframework.batch.core.ItemProcessListener;

import java.math.BigDecimal;

public class SumOfTaxesItemProcessListener implements ItemProcessListener<Employee, Employee> {
    private SumOfTaxes sumOfTaxes;

    public SumOfTaxesItemProcessListener(SumOfTaxes sumOfTaxes) {
        this.sumOfTaxes = sumOfTaxes;
    }

    @Override
    public void beforeProcess(Employee item) {
        System.out.println("\n\n\nno op for before item process listener\n\n\n\n");
    }

    @Override
    public void afterProcess(Employee item, Employee result) {
        //successful process of an Employee, add his taxes to the sum
        sumOfTaxes.addToSumOfTaxes(result.getIncomeTax());
        System.out.println("\n\n\nafter process\n\n\n");
    }

    @Override
    public void onProcessError(Employee item, Exception e) {
        //failed processing of an Employee, do not add his taxes to the sum
        System.out.println("\n\n\n\nno operation on process error\n\n\n\n");
    }
}