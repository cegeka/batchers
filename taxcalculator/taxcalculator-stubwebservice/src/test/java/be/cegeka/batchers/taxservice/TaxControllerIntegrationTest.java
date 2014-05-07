package be.cegeka.batchers.taxservice;

import be.cegeka.batchers.taxservice.config.WebAppConfigurationAware;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TaxControllerIntegrationTest extends WebAppConfigurationAware {

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


    @Test
    public void testUnluckEmployeesGetFailureResponse() throws Exception {
        mockMvc.perform(post("/taxservice").contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(new TaxTo("radu", 222.3)))
        ).andExpect(status().is4xxClientError());
    }


}
