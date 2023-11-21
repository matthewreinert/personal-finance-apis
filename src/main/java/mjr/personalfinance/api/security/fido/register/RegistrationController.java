package mjr.personalfinance.api.security.fido.register;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yubico.webauthn.exception.RegistrationFailedException;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "/api/webauthn/register", produces = MediaType.APPLICATION_JSON_VALUE)
public class RegistrationController {
    private static final String START_REG_REQUEST = "start_reg_request";
    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/start")
    RegistrationStartResponse startRegistration(@RequestBody RegistrationStartRequest request, HttpSession session)
            throws JsonProcessingException {
        var response = this.registrationService.startRegistration(request);
        session.setAttribute(START_REG_REQUEST, response);
        return response;
    }

    @PostMapping("/finish")
    RegistrationFinishResponse finishRegistration(@RequestBody RegistrationFinishRequest request, HttpSession session)
            throws RegistrationFailedException {
        RegistrationStartResponse response = (RegistrationStartResponse) session.getAttribute(START_REG_REQUEST);
        if (response == null) {
            throw new RuntimeException("Could not find the original request");
        }
        return this.registrationService.finishRegistration(request, response.credentialCreationOptions());
    }
}
