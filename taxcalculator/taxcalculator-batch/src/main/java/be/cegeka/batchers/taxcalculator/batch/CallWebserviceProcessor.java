package be.cegeka.batchers.taxcalculator.batch;

import be.cegeka.batchers.taxcalculator.application.domain.TaxCalculation;
import be.cegeka.batchers.taxcalculator.application.domain.TaxServiceCallResult;
import be.cegeka.batchers.taxcalculator.application.service.TaxPaymentWebService;
import be.cegeka.batchers.taxcalculator.batch.config.RetryConfig;
import be.cegeka.batchers.taxcalculator.batch.service.TaxPaymentWebServiceFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;

@Component
@Import(RetryConfig.class)
public class CallWebserviceProcessor implements ItemProcessor<TaxCalculation, TaxServiceCallResult> {
    private static final Logger LOG = LoggerFactory.getLogger(CallWebserviceProcessor.class);

    @Autowired
    private TaxPaymentWebService taxPaymentWebService;

    @Autowired
    private TaxPaymentWebServiceFacade taxPaymentWebServiceFacade;

    @Autowired
    private RetryConfig retryConfig;

    @Override
    public TaxServiceCallResult process(TaxCalculation taxCalculation) throws Exception {
        LOG.info("Web service process: " + taxCalculation);

        RetryTemplate retryTemplate = retryConfig.createRetryTemplate();
        Callable<TaxServiceCallResult> callable = () -> retryTemplate.execute(retryContext ->
                taxPaymentWebService.doWebserviceCallToTaxService(taxCalculation));

        TaxServiceCallResult taxServiceCallResult = taxPaymentWebServiceFacade.callTaxService(taxCalculation, callable);

        return taxServiceCallResult;
    }
}
