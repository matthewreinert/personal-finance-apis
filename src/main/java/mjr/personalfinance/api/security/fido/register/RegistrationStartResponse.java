package mjr.personalfinance.api.security.fido.register;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;

@JsonInclude(Include.NON_NULL)
public record RegistrationStartResponse(UUID flowId, PublicKeyCredentialCreationOptions credentialCreationOptions) {

}
