package mjr.personalfinance.api.security.fido.login;

import java.util.UUID;

import com.yubico.webauthn.AssertionRequest;

public record LoginStartResponse(UUID flowId, AssertionRequest assertionRequest) {

}
