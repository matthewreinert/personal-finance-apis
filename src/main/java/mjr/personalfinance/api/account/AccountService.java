package mjr.personalfinance.api.account;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
class AccountService {

    private final AccountRepository repository;

    AccountService(AccountRepository repository) {
        this.repository = repository;
    }

    Account createAccount(Account account) {
        return repository.save(account);
    }

    List<Account> getAllAccounts() {
        return repository.findAll();
    }

    Account findById(Integer accountId) {
        return repository.findById(accountId).orElseThrow(() -> new AccountNotFoundException(accountId));

    }

    Account updateAccount(Integer accountId, Account account) {
        return repository.findById(accountId).map(dbAccount -> {
            dbAccount.setName(account.getName());
            return repository.save(dbAccount);
        }).orElseGet(() -> {
            account.setId(accountId);
            return repository.save(account);
        });
    }

    void deleteAccount(Integer accountId) {
        repository.deleteById(accountId);
    }
}
