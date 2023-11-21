package mjr.personalfinance.api.security.fido.register;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import mjr.personalfinance.api.json.JsonUtils;

@Entity
@Table(name = "webauthn_registration_flow")
@Data
public class RegistrationFlowEntity {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "start_request", columnDefinition = "TEXT")
    private String startRequest;

    @Column(name = "start_response", columnDefinition = "TEXT")
    private String startResponse;

    @Column(name = "finish_request", columnDefinition = "TEXT")
    private String finishRequest;

    @Column(name = "finish_response", columnDefinition = "TEXT")
    private String finishResponse;

    @Column(name = "yubico_reg_result", columnDefinition = "TEXT")
    private String registrationResult;

    @Column(name = "yubico_creation_options", columnDefinition = "TEXT")
    private String creationOptions;

    @Override
    public String toString() {
        return JsonUtils.toJson(this);
    }
}
