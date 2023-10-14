package mjr.personalfinance.api.account;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
class Accounts {
    private List<Account> accounts;
}
