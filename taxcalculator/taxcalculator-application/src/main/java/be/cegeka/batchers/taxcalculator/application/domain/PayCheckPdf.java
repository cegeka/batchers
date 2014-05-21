package be.cegeka.batchers.taxcalculator.application.domain;

import javax.persistence.*;

@NamedQueries({
        @NamedQuery(name = PayCheckPdf.FIND_BY_TAXCALCULATION, query = PayCheckPdf.FIND_BY_TAXCALCULATION_QUERY)
})

@Entity
public class PayCheckPdf {
    public static final String FIND_BY_TAXCALCULATION = "PayCheckPdf.FIND_BY_TAXCALCULATION";
    public static final String FIND_BY_TAXCALCULATION_QUERY = "SELECT p from PayCheckPdf p WHERE p.taxCalculation.id = :taxCalculationId";

    @Id
    @GeneratedValue
    private Long id;

    @Lob
    private byte[] content;

    @OneToOne
    private TaxCalculation taxCalculation;

    public static PayCheckPdf from(TaxCalculation taxCalculation, byte[] content) {
        PayCheckPdf payCheckPdf = new PayCheckPdf();
        payCheckPdf.taxCalculation = taxCalculation;
        payCheckPdf.content = content;
        return payCheckPdf;
    }

    public byte[] getContent() {
        return content;
    }

    public TaxCalculation getTaxCalculation() {
        return taxCalculation;
    }

    public Long getId() {
        return id;
    }
}
