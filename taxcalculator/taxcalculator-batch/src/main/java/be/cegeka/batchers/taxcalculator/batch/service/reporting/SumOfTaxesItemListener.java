package be.cegeka.batchers.taxcalculator.batch.service.reporting;

import be.cegeka.batchers.taxcalculator.application.domain.PayCheck;
import be.cegeka.batchers.taxcalculator.application.domain.TaxCalculation;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.SkipListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SumOfTaxesItemListener implements ItemProcessListener<TaxCalculation, PayCheck>, SkipListener<TaxCalculation, PayCheck> {
    @Autowired
    private SumOfTaxes sumOfTaxes;

    @Override
    public void beforeProcess(TaxCalculation item) {
    }

    @Override
    public void afterProcess(TaxCalculation item, PayCheck result) {
        sumOfTaxes.addToSuccessSum(item.getEmployee().getIncomeTax());
    }

    @Override
    public void onProcessError(TaxCalculation item, Exception e) {
    }

    @Override
    public void onSkipInRead(Throwable t) {
    }

    @Override
    public void onSkipInWrite(PayCheck item, Throwable t) {
    }

    @Override
    public void onSkipInProcess(TaxCalculation item, Throwable t) {
        sumOfTaxes.addToFailingSum(item.getEmployee().getIncomeTax());
    }
}