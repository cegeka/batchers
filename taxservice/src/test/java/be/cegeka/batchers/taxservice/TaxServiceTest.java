package be.cegeka.batchers.taxservice;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: raduci
 * Date: 29.04.2014
 * Time: 14:42
 * To change this template use File | Settings | File Templates.
 */

@RunWith(MockitoJUnitRunner.class)
public class TaxServiceTest {

    @Mock
    TaxSubmissionLogger taxLogger;

    @InjectMocks
    TaxController taxController = new TaxController();

    @Test
    public void whenAValidTaxToIsReceived_ThenALogLineIsCreated(){
        //Arrange

        //Act
        TaxTo taxTo = new TaxTo("1234567",123.0);
        taxController.submitTaxForm(taxTo);

        //Assert
        verify(taxLogger, times(1)).log(taxTo, "OK");
    }
}
