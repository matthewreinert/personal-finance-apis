package mjr.apps.personalfinanceapis.account;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Account create(@RequestBody Account account) {
        return accountService.createAccount(account);
    }

    @GetMapping
    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @GetMapping("/{accountId}")
    public Account getAccountById(@PathVariable Long accountId) {
        return accountService.findById(accountId);
    }

    @PutMapping("/{accountId}")
    public Account updateAccountById(@PathVariable Long accountId, @RequestBody Account account) {
        return accountService.updateAccount(accountId, account);
    }

    @DeleteMapping("/{accountId}")
    public void deleteAccountById(@PathVariable Long accountId) {
        accountService.deleteAccount(accountId);
    }
}
