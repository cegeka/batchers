package be.cegeka.batchers.taxcalculator.application.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@NamedQueries({
        @NamedQuery(name = PayCheck.FIND_BY_TAXCALCULATION, query = PayCheck.FIND_BY_TAXCALCULATION_QUERY)
})

@Entity
public class PayCheck {
    public static final String FIND_BY_TAXCALCULATION = "PayCheck.FIND_BY_TAXCALCULATION";
    public static final String FIND_BY_TAXCALCULATION_QUERY = "SELECT p from PayCheck p WHERE p.taxCalculation.id = :taxCalculationId";

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    private TaxCalculation taxCalculation;

    @Lob
    private byte[] payCheckPdf;

    @NotNull
    private Long jobExecutionId;

    public static PayCheck from(TaxCalculation taxCalculation, byte[] payCheckPdf, Long jobExecutionId) {
        PayCheck payCheck = new PayCheck();
        payCheck.taxCalculation = taxCalculation;
        payCheck.payCheckPdf = payCheckPdf;
        payCheck.jobExecutionId = jobExecutionId;
        return payCheck;
    }

    public Long getId() {
        return id;
    }

    public TaxCalculation getTaxCalculation() {
        return taxCalculation;
    }

    public byte[] getPayCheckPdf() {
        return payCheckPdf;
    }

    public Long getJobExecutionId() {
        return jobExecutionId;
    }
}
