package mjr.apps.personalfinanceapis.account;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AccountRepository extends ReactiveCrudRepository<Account, Long> {
    
}
