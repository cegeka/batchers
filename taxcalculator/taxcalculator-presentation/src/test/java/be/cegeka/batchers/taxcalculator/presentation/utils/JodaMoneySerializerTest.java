package be.cegeka.batchers.taxcalculator.presentation.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class JodaMoneySerializerTest {

    @Mock
    private JsonGenerator jsonGenerator;

    @Test
    public void testMoneySerialization() throws IOException {
        JodaMoneySerializer jodaMoneySerializer = new JodaMoneySerializer();
        Money value = Money.of(CurrencyUnit.EUR, 234.56);
        jodaMoneySerializer.serialize(value, jsonGenerator, null);

        verify(jsonGenerator).writeString("234.56 €");
    }

}
