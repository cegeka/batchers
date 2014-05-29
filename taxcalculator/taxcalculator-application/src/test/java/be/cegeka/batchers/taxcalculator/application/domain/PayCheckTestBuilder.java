package be.cegeka.batchers.taxcalculator.application.domain;

public class PayCheckTestBuilder {

    private long jobExecutionId = 1L;
    private TaxCalculation taxCalculation = new TaxCalculationTestBuilder().build();
    private byte[] content;

    public PayCheck build() {
        return PayCheck.from(jobExecutionId, taxCalculation, content);
    }

    public PayCheckTestBuilder withJobExecutionId(long jobExecutionId) {
        this.jobExecutionId = jobExecutionId;
        return this;
    }

    public PayCheckTestBuilder withTaxCalculation(TaxCalculation taxCalculation) {
        this.taxCalculation = taxCalculation;
        return this;
    }

    public PayCheckTestBuilder withContent(byte[] content) {
        this.content = content;
        return this;
    }
}
