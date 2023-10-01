package mjr.apps.personalfinanceapis.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Account> create(@RequestBody Account account) {
        return accountService.createAccount(account);
    }

    @GetMapping
    public Flux<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @GetMapping("/{accountId}")
    public Mono<ResponseEntity<Account>> getAccountById(@PathVariable Long accountId) {
        Mono<Account> account = accountService.findById(accountId);
        return account.map(a -> ResponseEntity.ok(a)).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{accountId}")
    public Mono<ResponseEntity<Account>> updateAccountById(@PathVariable Long accountId, @RequestBody Account account){
        return accountService.updateAccount(accountId,account)
                .map(updatedAccount -> ResponseEntity.ok(updatedAccount))
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/{accountId}")
    public Mono<ResponseEntity<Void>> deleteAccountById(@PathVariable Long accountId){
        return accountService.deleteAccount(accountId)
                .map( r -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
