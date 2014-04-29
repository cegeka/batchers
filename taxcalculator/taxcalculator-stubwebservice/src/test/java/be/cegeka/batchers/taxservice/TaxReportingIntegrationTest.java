package be.cegeka.batchers.taxservice;

import be.cegeka.batchers.taxservice.config.WebAppConfigurationAware;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import org.springframework.http.MediaType;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created with IntelliJ IDEA.
 * User: raduci
 * Date: 29.04.2014
 * Time: 12:02
 * To change this template use File | Settings | File Templates.
 */
public class TaxReportingIntegrationTest extends WebAppConfigurationAware {


    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public static byte[] convertObjectToJsonBytes(Object object)
            throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsBytes(object);
    }

    @Test
    public void testOK() throws Exception {
        mockMvc.perform(post("/taxservice").contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(new TaxTo("1233454354", 222.3)))
        ).andExpect(status().isOk());
    }

    @Test
    public void testNoParams() throws Exception {
        mockMvc.perform(post("/taxservice").contentType(MediaType.APPLICATION_JSON)
                .content("")
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void testMissingEmployeeId() throws Exception {
        mockMvc.perform(post("/taxservice").contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(new TaxTo(null, 222.3)))
        ).andExpect(status().isBadRequest());


    }

    @Test
    public void testMissingAmount() throws Exception {
        mockMvc.perform(post("/taxservice").contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(new TaxTo("1234455667", null)))
        ).andExpect(status().isBadRequest());
    }


}
