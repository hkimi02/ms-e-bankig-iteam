package tn.iteam;

import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tn.iteam.entities.Account;
import tn.iteam.models.Customer;
import tn.iteam.proxy.CustomerOpenFeign;
import tn.iteam.repos.AccountRepository;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {"eureka.client.enabled=false", "spring.cloud.discovery.enabled=false"})
@AutoConfigureMockMvc
class AccountRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    // ms-customer est simulé : les tests ne dépendent pas d'Eureka ni d'un autre service
    @MockitoBean
    private CustomerOpenFeign customerOpenFeign;

    private String firstAccountIdOfCustomer1() {
        return accountRepository.findByCustomerId(1L).stream()
                .map(Account::getAccountId).findFirst().orElseThrow();
    }

    @Test
    void listsSeededAccounts() throws Exception {
        mockMvc.perform(get("/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void returnsAccountWithItsCustomer() throws Exception {
        when(customerOpenFeign.fetchCustomerDetails(1L))
                .thenReturn(new Customer(null, "Tarek", "Zemzmi", "tarek@zemzmi.com", "12345678"));

        mockMvc.perform(get("/accounts/" + firstAccountIdOfCustomer1()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.customer.firstName").value("Tarek"))
                .andExpect(jsonPath("$.customer.customerId").value(1));
    }

    @Test
    void returnsAccountsOfACustomer() throws Exception {
        when(customerOpenFeign.fetchCustomerDetails(1L))
                .thenReturn(new Customer(null, "Tarek", "Zemzmi", "tarek@zemzmi.com", "12345678"));

        mockMvc.perform(get("/accounts/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].customer.lastName", everyItem(is("Zemzmi"))));
    }

    @Test
    void returns404ForUnknownAccount() throws Exception {
        mockMvc.perform(get("/accounts/does-not-exist"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage", containsString("does-not-exist")));
    }

    @Test
    void returns404WhenCustomerMissingInMsCustomer() throws Exception {
        Request request = Request.create(Request.HttpMethod.GET, "/customers/99", Map.of(), null,
                StandardCharsets.UTF_8, null);
        when(customerOpenFeign.fetchCustomerDetails(anyLong()))
                .thenThrow(new FeignException.NotFound("not found", request, null, Map.of()));

        mockMvc.perform(get("/accounts/customer/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage", containsString("Customer")));
    }
}
