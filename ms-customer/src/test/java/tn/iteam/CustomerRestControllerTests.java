package tn.iteam;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {"eureka.client.enabled=false", "spring.cloud.discovery.enabled=false"})
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CustomerRestControllerTests {

    private static final String NEW_CUSTOMER = """
            {"firstName":"Ahmed","lastName":"Ben Ali","email":"ahmed@example.com","mobileNumber":"98765432"}
            """;

    @Autowired
    private MockMvc mockMvc;

    @Test @Order(1)
    void listsSeededCustomers() throws Exception {
        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].firstName").value("Tarek"));
    }

    @Test @Order(2)
    void createsCustomer() throws Exception {
        mockMvc.perform(post("/customers").contentType(MediaType.APPLICATION_JSON).content(NEW_CUSTOMER))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value("201"))
                .andExpect(jsonPath("$.statusMessage").value("Customer created successfully"));
    }

    @Test @Order(3)
    void rejectsDuplicateMobileNumber() throws Exception {
        mockMvc.perform(post("/customers").contentType(MediaType.APPLICATION_JSON).content(NEW_CUSTOMER))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", containsString("BAD_REQUEST")))
                .andExpect(jsonPath("$.apiPath").value("uri=/customers"))
                .andExpect(jsonPath("$.errorMessage", containsString("already exists")));
    }

    @Test @Order(4)
    void rejectsInvalidPayload() throws Exception {
        mockMvc.perform(post("/customers").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"\",\"lastName\":\"X\",\"email\":\"not-an-email\",\"mobileNumber\":\"12\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.firstName").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.mobileNumber").exists());
    }

    @Test @Order(5)
    void fetchesById() throws Exception {
        mockMvc.perform(get("/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mobileNumber").value("12345678"));
    }

    @Test @Order(6)
    void returns404ForUnknownId() throws Exception {
        mockMvc.perform(get("/customers/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", containsString("NOT_FOUND")));
    }

    @Test @Order(7)
    void fetchesByMobileNumber() throws Exception {
        mockMvc.perform(get("/customers/fetch").param("mobileNumber", "98765432"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Ahmed"));
    }

    @Test @Order(8)
    void updatesCustomer() throws Exception {
        mockMvc.perform(put("/customers").contentType(MediaType.APPLICATION_JSON).content("""
                        {"firstName":"Ahmed","lastName":"Ben Ali","email":"ahmed.benali@example.com","mobileNumber":"98765432"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value("200"));
        mockMvc.perform(get("/customers/fetch").param("mobileNumber", "98765432"))
                .andExpect(jsonPath("$.email").value("ahmed.benali@example.com"));
    }

    @Test @Order(9)
    void deletesCustomer() throws Exception {
        mockMvc.perform(delete("/customers/delete").param("mobileNumber", "98765432"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/customers/fetch").param("mobileNumber", "98765432"))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/customers/delete").param("mobileNumber", "98765432"))
                .andExpect(status().isNotFound());
    }
}
