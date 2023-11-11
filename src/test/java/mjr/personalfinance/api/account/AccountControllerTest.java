package mjr.personalfinance.api.account;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Test
    @WithAnonymousUser
    void itRequiresAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/accounts").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(accountService);
    }

    @WithMockUser()
    @Test
    void itReturnsAllAccounts() throws Exception {
        List<Account> accounts = List.of(new Account(1, "account1"),
                new Account(2, "account2"));
        when(accountService.getAllAccounts()).thenReturn(accounts);
        mockMvc.perform(get("/accounts").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType((MediaType.APPLICATION_JSON)))
                .andExpect(jsonPath("$.accounts[0].name").value("account1"))
                .andExpect(jsonPath("$.accounts[1].name").value("account2"));
        verify(accountService, times(1)).getAllAccounts();
    }

    @WithMockUser()
    @Test
    void itReturnsOneAccounts() throws Exception {
        Account expectedAccount = new Account(42, "account1");
        when(accountService.findById(42)).thenReturn(expectedAccount);
        mockMvc.perform(get("/accounts/42").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType((MediaType.APPLICATION_JSON)))
                .andExpect(jsonPath("$.name").value("account1"))
                .andExpect(jsonPath("$.id").value(42));
    }

    @WithMockUser()
    @Test
    void itCreatesAccount() throws Exception {
        Account newAccount = new Account();
        newAccount.setName("account name");

        Account expectedAccount = new Account(42, "account name");
        when(accountService.createAccount(newAccount)).thenReturn(expectedAccount);
        mockMvc.perform(
                post("/accounts")
                        .content(asJsonString(newAccount))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(content().contentType((MediaType.APPLICATION_JSON)))
                .andExpect(jsonPath("$.name").value("account name"))
                .andExpect(jsonPath("$.id").value(42));
    }

    @WithMockUser()
    @Test
    void itUpdatesAccount() throws Exception {
        Account expectedAccount = new Account(42, "account name");
        when(accountService.updateAccount(42, expectedAccount)).thenReturn(expectedAccount);

        mockMvc.perform(
                put("/accounts/42")
                        .content(asJsonString(expectedAccount))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType((MediaType.APPLICATION_JSON)))
                .andExpect(jsonPath("$.name").value("account name"))
                .andExpect(jsonPath("$.id").value(42));
    }

    @WithMockUser()
    @Test
    void itDeletesAccount() throws Exception {
        mockMvc.perform(
                delete("/accounts/42")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk());
        verify(accountService, times(1)).deleteAccount(42);
    }

    static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}