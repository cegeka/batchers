package be.cegeka.batchers.taxcalculator.presentation.rest.model;

import be.cegeka.batchers.taxcalculator.application.util.jackson.JodaDateTimeSerializer;
import be.cegeka.batchers.taxcalculator.presentation.utils.JodaMoneySerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.joda.money.Money;
import org.joda.time.DateTime;

public class EmployeeTaxTo {
    private int year;
    private int month;

    @JsonSerialize(using = JodaMoneySerializer.class)
    private Money tax;

    @JsonSerialize(using = JodaDateTimeSerializer.class)
    private DateTime calculationDate;

    private String status;

    public EmployeeTaxTo(int year, int month, Money tax, DateTime calculationDate, String status) {
        this.year = year;
        this.month = month;
        this.tax = tax;
        this.calculationDate = calculationDate;
        this.status = status;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public String getStatus() {
        return status;
    }

    public Money getTax() {
        return tax;
    }
}
