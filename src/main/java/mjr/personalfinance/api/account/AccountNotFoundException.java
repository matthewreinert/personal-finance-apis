package mjr.personalfinance.api.account;

class AccountNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 7020945971273897041L;

	AccountNotFoundException(Integer id) {
		super("Could not find account: " + id);
	}
}
