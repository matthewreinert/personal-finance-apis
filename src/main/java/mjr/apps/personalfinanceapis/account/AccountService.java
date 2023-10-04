package mjr.apps.personalfinanceapis.account;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    @Autowired
    private AccountRepository repository;

    public Account createAccount(Account account) {
        return repository.save(account);
    }

    public List<Account> getAllAccounts() {
        return repository.findAll();
    }

    public Account findById(Long accountId) {
        return repository.findById(accountId).get();
    }

    public Account updateAccount(Long accountId, Account account) {
        return repository.findById(accountId).map(dbAccount -> {
            dbAccount.setName(account.getName());
            return repository.save(dbAccount);
        }).orElseGet(() -> {
            account.setId(accountId);
            return repository.save(account);
        });
    }

    public void deleteAccount(Long accountId) {
        repository.deleteById(accountId);
    }
}
