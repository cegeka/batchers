package be.cegeka.batchers.taxcalculator.application.service;

import be.cegeka.batchers.taxcalculator.application.domain.Employee;
import be.cegeka.batchers.taxcalculator.application.domain.TaxCalculation;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;

@Service
public class TaxCalculatorService {

    @Autowired
    private RunningTimeService runningTimeService;

    public TaxCalculation calculateTax(long jobExecutionId, Employee employee, int year, int month) {
        runningTimeService.sleep();
        double taxAmount = employee.getIncome() * 0.1;
        Money tax = Money.of(CurrencyUnit.EUR, taxAmount, RoundingMode.HALF_DOWN);

        return TaxCalculation.from(jobExecutionId, employee, year, month, tax);
    }

    public void setRunningTimeService(RunningTimeService runningTimeService) {
        this.runningTimeService = runningTimeService;
    }
}
