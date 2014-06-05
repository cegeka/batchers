package be.cegeka.batchers.taxcalculator.batch.domain;

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

    @NotNull
    private Long jobExecutionId;

    @OneToOne
    private TaxCalculation taxCalculation;

    @Lob
    private byte[] payCheckPdf;

    public static PayCheck from(Long jobExecutionId, TaxCalculation taxCalculation, byte[] payCheckPdf) {
        PayCheck payCheck = new PayCheck();
        payCheck.jobExecutionId = jobExecutionId;
        payCheck.taxCalculation = taxCalculation;
        payCheck.payCheckPdf = payCheckPdf;
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
