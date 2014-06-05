package be.cegeka.batchers.taxcalculator.application.domain.taxpayment;

import javax.validation.constraints.NotNull;

public class TaxTo {

    @NotNull
    private Long employeeId;

    @NotNull
    private Double amount;

    public TaxTo() {
    }

    public TaxTo(Long employeeId, Double amount) {
        this.employeeId = employeeId;
        this.amount = amount;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setEmployeeId(Long employeeId) {
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
