package mjr.personalfinance.api.security.fido.login;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yubico.webauthn.AssertionRequest;
import com.yubico.webauthn.AssertionResult;
import com.yubico.webauthn.exception.AssertionFailedException;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping(value = "/api/webauthn/login", produces = MediaType.APPLICATION_JSON_VALUE)
public class LoginController {
    private static final String START_REG_REQUEST = "start_login_request";
    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @ResponseBody
    @PostMapping("/start")
    LoginStartResponse startLogin(
            @RequestBody LoginStartRequest request, HttpSession session) {
        var response = this.loginService.startLogin(request);
        session.setAttribute(START_REG_REQUEST, response.assertionRequest());
        return response;
    }

    @ResponseBody
    @PostMapping("/finish")
    AssertionResult finishLogin(@RequestBody LoginFinishRequest request, HttpSession session)
            throws AssertionFailedException {
        var assertionRequest = (AssertionRequest) session.getAttribute(START_REG_REQUEST);
        if (assertionRequest == null) {
            throw new RuntimeException("Cloud Not find the original request");
        }

        var result = this.loginService.finishLogin(request);
        if (result.isSuccess()) {
            session.setAttribute(AssertionRequest.class.getName(), result);
        }
        return result;
    }
}
