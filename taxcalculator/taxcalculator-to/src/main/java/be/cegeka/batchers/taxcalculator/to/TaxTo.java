package be.cegeka.batchers.taxcalculator.to;

import javax.validation.constraints.NotNull;

public class TaxTo {

    @NotNull
    private String employeeId;

    @NotNull
    private Double amount;

    public TaxTo() {
    }

    public TaxTo(String employeeId, Double amount) {
        this.employeeId = employeeId;
        this.amount = amount;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "TaxTo{" +
                "employeeId='" + employeeId + '\'' +
                ", amount=" + amount +
                '}';
    }
}
