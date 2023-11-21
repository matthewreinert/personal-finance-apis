package mjr.personalfinance.api.security.fido.register;

import java.util.UUID;

public record RegistrationFinishResponse(UUID flowId, boolean registrationComplete) {
}
