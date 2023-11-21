package mjr.personalfinance.api.security.user;

import java.util.Set;
import java.util.UUID;

/**
 * Basic representation of a user that is exposed outside this package. It is
 * used to associate a set of FIDO
 * credentials i.e. authenticators that can be used to log in a user. For
 * example, the user can configure thier
 * account to log in with faceID from an iPhone and using a YubiKey and using
 * the fingerprint scanner on a laptop.
 *
 * Manipulating a UserAccount is done via an instance of a UserService
 *
 * @param id          the unique uuid that identifies the user, this value is
 *                    auto generated by UserService
 * @param displayName the username that will be displayed publicly to other
 *                    users
 * @param email       the email address of the user they will login with this
 *                    email
 * @param credentials a set of fido credentials that the user can login with.
 */
public record UserAccount(UUID id, String displayName, String email, Set<FidoCredential> credentials) {
}
