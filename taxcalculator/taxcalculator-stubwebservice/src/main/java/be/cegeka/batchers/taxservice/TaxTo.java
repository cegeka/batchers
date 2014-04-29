package be.cegeka.batchers.taxservice;

import javax.validation.constraints.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: raduci
 * Date: 29.04.2014
 * Time: 12:20
 * To change this template use File | Settings | File Templates.
 */
public class TaxTo {

    @NotNull
    private String employeeId;

    @NotNull
    private Double amount;

    public TaxTo() {}

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
