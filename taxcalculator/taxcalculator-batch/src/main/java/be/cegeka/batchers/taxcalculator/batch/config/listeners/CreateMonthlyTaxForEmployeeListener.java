package be.cegeka.batchers.taxcalculator.batch.config.listeners;

import be.cegeka.batchers.taxcalculator.application.domain.MonthlyTaxForEmployee;
import be.cegeka.batchers.taxcalculator.application.domain.MonthlyTaxForEmployeeRepository;
import be.cegeka.batchers.taxcalculator.application.domain.PayCheck;
import be.cegeka.batchers.taxcalculator.application.domain.TaxCalculation;
import be.cegeka.batchers.taxcalculator.application.service.TaxWebServiceException;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.listener.ItemListenerSupport;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

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
