package mjr.personalfinance.api.security.user;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "webauthn_user_credentials")
@Data
public class FidoCredentialEntity {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "type")
    private String type;

    @Column(name = "public_key_cose", columnDefinition = "TEXT")
    private String publicKeyCose;

}
