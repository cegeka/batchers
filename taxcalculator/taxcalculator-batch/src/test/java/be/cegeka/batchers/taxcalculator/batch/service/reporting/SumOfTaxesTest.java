package be.cegeka.batchers.taxcalculator.batch.service.reporting;

import be.cegeka.batchers.taxcalculator.batch.domain.TaxCalculationRepository;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SumOfTaxesTest {
    public static final Money MONEY_100Euro = Money.of(CurrencyUnit.EUR, 100D);
    public static final int TEST_YEAR = 2015;
    public static final int TEST_MONTH = 3;

    @InjectMocks
    private SumOfTaxes sumOfTaxes;

    @Mock
    private TaxCalculationRepository taxCalculationRepository;

    @Test
    public void testGetSuccessSum() throws Exception {
        when(taxCalculationRepository.getSuccessSum(TEST_YEAR, TEST_MONTH)).thenReturn(MONEY_100Euro);

        double successSum = sumOfTaxes.getSuccessSum(TEST_YEAR, TEST_MONTH);

        verify(taxCalculationRepository).getSuccessSum(TEST_YEAR, TEST_MONTH);
        assertTrue(successSum == MONEY_100Euro.getAmount().doubleValue());
    }

    @Test
    public void testGetFailedSum() throws Exception {
        when(taxCalculationRepository.getFailedSum(TEST_YEAR, TEST_MONTH)).thenReturn(MONEY_100Euro);

        double failedSum = sumOfTaxes.getFailedSum(TEST_YEAR, TEST_MONTH);

        verify(taxCalculationRepository).getFailedSum(TEST_YEAR, TEST_MONTH);
        assertTrue(failedSum == MONEY_100Euro.getAmount().doubleValue());
    }
}