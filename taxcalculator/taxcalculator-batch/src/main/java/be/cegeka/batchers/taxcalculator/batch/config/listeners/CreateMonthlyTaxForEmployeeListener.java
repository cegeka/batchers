package be.cegeka.batchers.taxcalculator.batch.config.listeners;

import be.cegeka.batchers.taxcalculator.application.domain.MonthlyTaxForEmployee;
import be.cegeka.batchers.taxcalculator.application.repositories.MonthlyTaxForEmployeeRepository;
import be.cegeka.batchers.taxcalculator.batch.domain.PayCheck;
import be.cegeka.batchers.taxcalculator.batch.domain.TaxCalculation;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class CreateMonthlyTaxForEmployeeListener implements ItemProcessListener<TaxCalculation, PayCheck> {

    @Autowired
    private MonthlyTaxForEmployeeRepository monthlyTaxForEmployeeRepository;

    @Override
    public void afterProcess(TaxCalculation item, PayCheck result) {
        MonthlyTaxForEmployee monthlyTaxForEmployee = MonthlyTaxForEmployee.from(item.getEmployee(), item.getYear(), item.getMonth(), item.getTax(), result.getPayCheckPdf());
        monthlyTaxForEmployeeRepository.save(monthlyTaxForEmployee);
    }

    @Override
    public void onProcessError(TaxCalculation item, Exception e) {
        MonthlyTaxForEmployee monthlyTaxForEmployee = MonthlyTaxForEmployee.from(item.getEmployee(), item.getYear(), item.getMonth(), item.getTax(), e.getMessage());
        monthlyTaxForEmployeeRepository.save(monthlyTaxForEmployee);
    }

    @Override
    public void beforeProcess(TaxCalculation item) {
        // not interested
    }
}
