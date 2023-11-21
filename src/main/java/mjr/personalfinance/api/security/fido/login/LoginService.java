package mjr.personalfinance.api.security.fido.login;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yubico.webauthn.AssertionRequest;
import com.yubico.webauthn.AssertionResult;
import com.yubico.webauthn.FinishAssertionOptions;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.StartAssertionOptions;
import com.yubico.webauthn.exception.AssertionFailedException;

import mjr.personalfinance.api.json.JsonUtils;
import mjr.personalfinance.api.security.user.UserAccount;
import mjr.personalfinance.api.security.user.UserService;

@Service
class LoginService {

    private final RelyingParty relyingParty;
    private final LoginFlowRepository loginFlowRepository;
    private final UserService userService;

    public LoginService(
            RelyingParty relyingParty, LoginFlowRepository loginFlowRepository, UserService userService) {
        this.relyingParty = relyingParty;
        this.loginFlowRepository = loginFlowRepository;
        this.userService = userService;
    }

    /**
     * This method is used to determine if a user exists and then sends back to the
     * browser a list of
     * public keys that can be used to log in this way the browser can pick the
     * right authenticator and
     * complete the login process. The response includes a math challenge that the
     * authenticator needs
     * to solve using the users private key so that the server can tell that the
     * user is who they say
     * they are.
     *
     * @param loginStartRequest info containing the user that wants to login
     * @return configuration for the browser to use to interact with the FIDO2
     *         authenticator using WebAuthn browser API
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public LoginStartResponse startLogin(LoginStartRequest loginStartRequest) {
        // Find the user in the user database
        UserAccount user = this.userService
                .findUserEmail(loginStartRequest.email())
                .orElseThrow(() -> new RuntimeException("User does not exist"));

        System.out.println(user);
        // make the assertion request to send to the client
        StartAssertionOptions options = StartAssertionOptions.builder()
                .timeout(60_000)
                .username(loginStartRequest.email())
                // .userHandle(YubicoUtils.toByteArray(user.id()))
                .build();
        AssertionRequest assertionRequest = this.relyingParty.startAssertion(options);

        LoginStartResponse loginStartResponse = new LoginStartResponse(UUID.randomUUID(), assertionRequest);

        LoginFlowEntity loginFlowEntity = new LoginFlowEntity();
        loginFlowEntity.setId(loginStartResponse.flowId());
        loginFlowEntity.setStartRequest(JsonUtils.toJson(loginStartRequest));
        loginFlowEntity.setStartResponse(JsonUtils.toJson(loginStartResponse));
        try {
            loginFlowEntity.setAssertionRequest(assertionRequest.toJson());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        this.loginFlowRepository.save(loginFlowEntity);

        return loginStartResponse;
    }

    /**
     * Receives the solution to the math challenge from the start method, validates
     * that the solution is correct
     * applies the validation logic of the FIDO protocol, and then it produces a
     * result.
     *
     * @param loginFinishRequest
     * @return
     * @throws AssertionFailedException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AssertionResult finishLogin(LoginFinishRequest loginFinishRequest) throws AssertionFailedException {
        var loginFlowEntity = this.loginFlowRepository
                .findById(loginFinishRequest.flowId())
                .orElseThrow(() -> new RuntimeException("flow id " + loginFinishRequest.flowId() + " not found"));

        var assertionRequestJson = loginFlowEntity.getAssertionRequest();
        AssertionRequest assertionRequest = null;
        try {
            assertionRequest = AssertionRequest.fromJson(assertionRequestJson);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Could not deserialize the assertion request");
        }

        FinishAssertionOptions options = FinishAssertionOptions.builder()
                .request(assertionRequest)
                .response(loginFinishRequest.credential())
                .build();

        AssertionResult assertionResult = this.relyingParty.finishAssertion(options);

        loginFlowEntity.setAssertionResult(JsonUtils.toJson(assertionResult));
        loginFlowEntity.setSuccessfulLogin(assertionResult.isSuccess());

        return assertionResult;
    }

}
