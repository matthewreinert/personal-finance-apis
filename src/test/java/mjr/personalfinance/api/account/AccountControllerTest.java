package mjr.personalfinance.api.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Test
    void itReturnsAllAccounts() throws Exception {
        List<Account> accounts = List.of(new Account(1L, "account1"),
                new Account(2L, "account2"));
        when(accountService.getAllAccounts()).thenReturn(accounts);
        mockMvc.perform(get("/accounts").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType((MediaType.APPLICATION_JSON)))
                .andExpect(jsonPath("$.accounts[0].name").value("account1"))
                .andExpect(jsonPath("$.accounts[1].name").value("account2"));
    }

    @Test
    void itReturnsOneAccounts() throws Exception {
        Account expectedAccount = new Account(42L, "account1");
        when(accountService.findById(42L)).thenReturn(expectedAccount);
        mockMvc.perform(get("/accounts/42").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType((MediaType.APPLICATION_JSON)))
                .andExpect(jsonPath("$.name").value("account1"))
                .andExpect(jsonPath("$.id").value(42));
    }

    @Test
    void itCreatesAccount() throws Exception {
        Account newAccount = new Account();
        newAccount.setName("account name");

        Account expectedAccount = new Account(42L, "account name");
        when(accountService.createAccount(newAccount)).thenReturn(expectedAccount);
        mockMvc.perform(
                    post("/accounts")
                    .content(asJsonString(newAccount))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType((MediaType.APPLICATION_JSON)))
                .andExpect(jsonPath("$.name").value("account name"))
                .andExpect(jsonPath("$.id").value(42));
    }

    @Test
    void itUpdatesAccount() throws Exception {
        Account expectedAccount = new Account(42L, "account name");
        when(accountService.updateAccount(42L, expectedAccount)).thenReturn(expectedAccount);

        mockMvc.perform(
                        put("/accounts/42")
                                .content(asJsonString(expectedAccount))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType((MediaType.APPLICATION_JSON)))
                .andExpect(jsonPath("$.name").value("account name"))
                .andExpect(jsonPath("$.id").value(42));
    }

    @Test
    void itDeletesAccount() throws Exception {
        mockMvc.perform(delete("/accounts/42").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(accountService, times(1)).deleteAccount(42L);
    }

    static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}