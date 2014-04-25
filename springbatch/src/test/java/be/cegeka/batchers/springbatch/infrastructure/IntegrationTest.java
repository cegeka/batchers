package be.cegeka.batchers.springbatch.infrastructure;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = "classpath:/be/cegeka/batchers/springbatch/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class IntegrationTest {
}
