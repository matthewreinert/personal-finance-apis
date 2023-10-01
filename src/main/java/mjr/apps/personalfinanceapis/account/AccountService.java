package mjr.apps.personalfinanceapis.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Transactional
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public Mono<Account> createAccount(Account account) {
        return accountRepository.save(account);
    }

    public Flux<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Mono<Account> findById(Long accountId) {
        return accountRepository.findById(accountId);
    }

    public Mono<Account> updateAccount(Long accountId, Account account) {
        return accountRepository.findById(accountId).flatMap(dbAccount -> {
            dbAccount.setName(account.getName());
            return accountRepository.save(dbAccount);
        });
    }

    public Mono<Account> deleteAccount(Long accountId) {
        return accountRepository.findById(accountId)
                .flatMap(existingAccount -> accountRepository.delete(existingAccount).then(Mono.just(existingAccount)));
    }
}
