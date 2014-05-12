package be.cegeka.batchers.taxcalculator.to;

public class TaxServiceResponse {
    public String status;

    public TaxServiceResponse() {

    }

    public TaxServiceResponse(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaxServiceResponse)) return false;

        TaxServiceResponse that = (TaxServiceResponse) o;

        if (status != null ? !status.equals(that.status) : that.status != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return status != null ? status.hashCode() : 0;
    }
}