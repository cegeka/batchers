package be.cegeka.batchers.taxcalculator;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * <p>
 * Example test case transforming a {@link Person} using the {@link PersonItemProcessor}.
 * </p>
 */
public class PersonItemProcessorTest {
    @Test
    public void testProcessedPersonRecord() throws Exception {
        Person person = new Person();
        person.setFirstName("Jane");
        person.setLastName("Doe");

        Person processedPerson = new PersonItemProcessor().process(person);

        assertEquals("JANE", processedPerson.getFirstName());
        assertEquals("DOE", processedPerson.getLastName());
    }
}
