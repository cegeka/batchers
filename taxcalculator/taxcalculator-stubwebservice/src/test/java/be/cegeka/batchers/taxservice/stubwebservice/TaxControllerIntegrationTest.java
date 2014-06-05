package be.cegeka.batchers.taxservice.stubwebservice;

import be.cegeka.batchers.taxservice.stubwebservice.config.WebAppTestConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TaxControllerIntegrationTest extends WebAppTestConfig {

    public static byte[] convertObjectToJsonBytes(Object object)
            throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsBytes(object);
    }

    @Test
    public void testOK() throws Exception {
        mockMvc.perform(post("/taxservice").contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(createTaxToAsJsonNode(1233454354L, 222.3)))
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
                .content(convertObjectToJsonBytes(createTaxToAsJsonNode(null, 222.3)))
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void testMissingAmount() throws Exception {
        mockMvc.perform(post("/taxservice").contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(createTaxToAsJsonNode(1234455667L, null)))
        ).andExpect(status().isBadRequest());
    }


    @Test
    public void testUnluckEmployeesGetFailureResponse() throws Exception {
        mockMvc.perform(post("/taxservice").contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(createTaxToAsJsonNode(123L, 222.3)))
        ).andExpect(status().is5xxServerError());
    }

    @Test
    public void testResetSpecialEmployeesService() throws Exception {
        mockMvc.perform(post("/reset")).andExpect(status().isOk());
    }

    private JsonNode createTaxToAsJsonNode(Long employeeId, Double amount) {
        ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
        objectNode.put("employeeId", employeeId);
        objectNode.put("amount", amount);
        return objectNode;
    }
}
