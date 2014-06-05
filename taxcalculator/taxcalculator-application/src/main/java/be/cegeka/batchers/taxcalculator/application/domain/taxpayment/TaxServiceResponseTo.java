package be.cegeka.batchers.taxcalculator.application.domain.taxpayment;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;

public class TaxServiceResponseTo {
    private String status;

    public TaxServiceResponseTo() {
        //needed for Jackson
    }

    public TaxServiceResponseTo(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object that) {
        return EqualsBuilder.reflectionEquals(this, that);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}