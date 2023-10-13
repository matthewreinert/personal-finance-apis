package mjr.personalfinance.api.account;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
class AccountController {

    private final AccountService service;
    
    AccountController(AccountService service) {
    	this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Account create(@RequestBody Account account) {
        return service.createAccount(account);
    }

    @GetMapping
    Accounts getAllAccounts() {
        List<Account> allAccounts = service.getAllAccounts();
        return Accounts.builder().accounts(allAccounts).build();
    }

    @GetMapping("/{accountId}")
    Account getAccountById(@PathVariable Long accountId) {
        return service.findById(accountId);
    }

    @PutMapping("/{accountId}")
    Account updateAccountById(@PathVariable Long accountId, @RequestBody Account account) {
        return service.updateAccount(accountId, account);
    }

    @DeleteMapping("/{accountId}")
    void deleteAccountById(@PathVariable Long accountId) {
        service.deleteAccount(accountId);
    }
}
