package mjr.personalfinance.api.security.user;

import java.util.Optional;
import java.util.UUID;

public interface UserService {

    UserAccount createOrFindUser(String displayName, String email);

    Optional<UserAccount> findUserEmail(String email);

    Optional<UserAccount> findUserById(UUID userId);

    Optional<FidoCredential> findCredentialById(String credentialId);

    void addCredential(FidoCredential fidoCredential);

}
