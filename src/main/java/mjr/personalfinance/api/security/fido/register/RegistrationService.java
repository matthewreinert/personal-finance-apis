package mjr.personalfinance.api.security.fido.register;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yubico.webauthn.FinishRegistrationOptions;
import com.yubico.webauthn.RegistrationResult;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.StartRegistrationOptions;
import com.yubico.webauthn.data.AuthenticatorSelectionCriteria;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.data.UserIdentity;
import com.yubico.webauthn.data.UserVerificationRequirement;
import com.yubico.webauthn.exception.RegistrationFailedException;

import mjr.personalfinance.api.json.JsonUtils;
import mjr.personalfinance.api.security.fido.yubico.YubicoUtils;
import mjr.personalfinance.api.security.user.FidoCredential;
import mjr.personalfinance.api.security.user.UserAccount;
import mjr.personalfinance.api.security.user.UserService;

@Service
public class RegistrationService {

        private UserService userService;
        private RelyingParty relyingParty;
        private RegistrationFlowRepository registrationFlowRepository;

        public RegistrationService(UserService userService, RelyingParty relyingParty,
                        RegistrationFlowRepository registrationFlowRepository) {
                this.userService = userService;
                this.relyingParty = relyingParty;
                this.registrationFlowRepository = registrationFlowRepository;
        }

        @Transactional(propagation = Propagation.REQUIRED)
        public RegistrationStartResponse startRegistration(RegistrationStartRequest startRequest)
                        throws JsonProcessingException {
                UserAccount user = this.userService.createOrFindUser(startRequest.name(), startRequest.email());
                PublicKeyCredentialCreationOptions options = createPublicKeyCredentialOptions(user);
                RegistrationStartResponse startResponse = createRegistrationStartResponse(options);
                logWorkflow(startRequest, startResponse);
                return startResponse;
        }

        private PublicKeyCredentialCreationOptions createPublicKeyCredentialOptions(UserAccount user) {
                UserIdentity userIdentity = UserIdentity.builder()
                                .name(user.email())
                                .displayName(user.displayName())
                                .id(YubicoUtils.toByteArray(user.id()))
                                .build();

                AuthenticatorSelectionCriteria authenticatorSelectionCriteria = AuthenticatorSelectionCriteria.builder()
                                .userVerification(UserVerificationRequirement.DISCOURAGED)
                                .build();

                StartRegistrationOptions startRegistrationOptions = StartRegistrationOptions.builder()
                                .user(userIdentity)
                                .timeout(30_000)
                                .authenticatorSelection(authenticatorSelectionCriteria)
                                .build();

                return this.relyingParty.startRegistration(startRegistrationOptions);
        }

        private RegistrationStartResponse createRegistrationStartResponse(PublicKeyCredentialCreationOptions options) {
                return new RegistrationStartResponse(UUID.randomUUID(), options);
        }

        private void logWorkflow(RegistrationStartRequest startRequest, RegistrationStartResponse startResponse)
                        throws JsonProcessingException {
                var registrationEntity = new RegistrationFlowEntity();
                registrationEntity.setId(startResponse.flowId());
                registrationEntity.setStartRequest(JsonUtils.toJson(startRequest));
                registrationEntity.setStartResponse(JsonUtils.toJson(startResponse));
                registrationEntity.setRegistrationResult(startResponse.credentialCreationOptions().toJson());
                this.registrationFlowRepository.save(registrationEntity);
        }

        @Transactional(propagation = Propagation.REQUIRED)
        public RegistrationFinishResponse finishRegistration(RegistrationFinishRequest finishRequest,
                        PublicKeyCredentialCreationOptions credentialCreationOptions)
                        throws RegistrationFailedException {
                FinishRegistrationOptions options = FinishRegistrationOptions.builder()
                                .request(credentialCreationOptions)
                                .response(finishRequest.credential())
                                .build();
                RegistrationResult registrationResult = this.relyingParty.finishRegistration(options);

                var fidoCredential = new FidoCredential(
                                registrationResult.getKeyId().getId().getBase64Url(),
                                registrationResult.getKeyId().getType().name(),
                                YubicoUtils.toUUID(credentialCreationOptions.getUser().getId()),
                                registrationResult.getPublicKeyCose().getBase64Url());

                this.userService.addCredential(fidoCredential);

                RegistrationFinishResponse registrationFinishResponse = new RegistrationFinishResponse(
                                finishRequest.flowId(),
                                true);

                logFinishStep(finishRequest, registrationResult, registrationFinishResponse);
                return registrationFinishResponse;
        }

        private void logFinishStep(RegistrationFinishRequest finishRequest, RegistrationResult registrationResult,
                        RegistrationFinishResponse registrationFinishResponse) {
                RegistrationFlowEntity registrationFlow = this.registrationFlowRepository
                                .findById(finishRequest.flowId())
                                .orElseThrow(() -> new RuntimeException(
                                                "Cloud not find a registration flow with id: "
                                                                + finishRequest.flowId()));
                registrationFlow.setFinishRequest(JsonUtils.toJson(finishRequest));
                registrationFlow.setFinishResponse(JsonUtils.toJson(registrationFinishResponse));
                registrationFlow.setRegistrationResult(JsonUtils.toJson(registrationResult));
        }
}
