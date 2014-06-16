package be.cegeka.batchers.taxcalculator.batch.processor;

import be.cegeka.batchers.taxcalculator.application.service.TaxPaymentWebService;
import be.cegeka.batchers.taxcalculator.application.service.exceptions.TaxWebServiceException;
import be.cegeka.batchers.taxcalculator.batch.config.RetryConfig;
import be.cegeka.batchers.taxcalculator.batch.domain.TaxCalculation;
import be.cegeka.batchers.taxcalculator.batch.domain.TaxWebserviceCallResult;
import be.cegeka.batchers.taxcalculator.batch.service.TaxPaymentWebServiceFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;

@Component
@Import(RetryConfig.class)
public class CallWebserviceProcessor implements ItemProcessor<TaxCalculation, TaxWebserviceCallResult> {
    private static final Logger LOG = LoggerFactory.getLogger(CallWebserviceProcessor.class);

    @Autowired
    private TaxPaymentWebService taxPaymentWebService;

    @Autowired
    private TaxPaymentWebServiceFacade taxPaymentWebServiceFacade;

    @Autowired
    private RetryConfig retryConfig;

    @Override
    public TaxWebserviceCallResult process(TaxCalculation taxCalculation) throws Exception {
        LOG.info("Web service process: " + taxCalculation);

        RetryTemplate retryTemplate = retryConfig.createRetryTemplate();
        Callable<Void> callable = () -> retryTemplate.execute(doWebserviceCallWithRetryCallback(taxCalculation));

        TaxWebserviceCallResult taxWebserviceCallResult = taxPaymentWebServiceFacade.callTaxService(taxCalculation, callable);

        return taxWebserviceCallResult;
    }

    private RetryCallback<Void, TaxWebServiceException> doWebserviceCallWithRetryCallback(TaxCalculation taxCalculation) {
        return new RetryCallback<Void, TaxWebServiceException>() {
            @Override
            public Void doWithRetry(RetryContext context) throws TaxWebServiceException {
                taxPaymentWebService.doWebserviceCallToTaxService(taxCalculation.getEmployee(), taxCalculation.getTax());
                return null;
            }
        };
    }
}
