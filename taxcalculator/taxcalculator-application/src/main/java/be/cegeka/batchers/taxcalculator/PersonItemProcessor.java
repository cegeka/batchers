package be.cegeka.batchers.taxcalculator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.ItemProcessor;

/**
 * <p>
 * An example {@link ItemProcessor} implementation that upper cases fields on the provided {@link Person} object.
 * </p>
 * <p/>
 * <p>
 * NOTE: {@link ItemProcessor}'s are optional, but here to serve as an example.
 * </p>
 */
public class PersonItemProcessor implements ItemProcessor<Person, Person> {
    private static Log LOG = LogFactory.getLog(PersonItemProcessor.class);

    @Override
    public Person process(Person person) throws Exception {
        String firstName = person.getFirstName().toUpperCase();
        String lastName = person.getLastName().toUpperCase();

        Person transformedPerson = new Person();
        transformedPerson.setFirstName(firstName);
        transformedPerson.setLastName(lastName);

        LOG.info("Transformed person: " + person + " Into: " + transformedPerson);

        return transformedPerson;
    }
}
