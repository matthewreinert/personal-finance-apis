package mjr.personalfinance.api.security.fido.login;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import mjr.personalfinance.api.json.JsonUtils;

@Entity
@Table(name = "webauthn_login_flow")
@Data
public class LoginFlowEntity {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "start_request", columnDefinition = "TEXT")
    private String startRequest;

    @Column(name = "start_response", columnDefinition = "TEXT")
    private String startResponse;

    @Column(name = "successful_login", columnDefinition = "TEXT")
    private Boolean successfulLogin;

    @Column(name = "assertion_request", columnDefinition = "TEXT")
    private String assertionRequest;

    @Column(name = "assertion_result", columnDefinition = "TEXT")
    private String assertionResult;

    @Override
    public String toString() {
        return JsonUtils.toJson(this);
    }
}
