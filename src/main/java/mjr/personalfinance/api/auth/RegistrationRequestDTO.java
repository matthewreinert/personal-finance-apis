package mjr.personalfinance.api.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequestDTO {
    private String firstName;
    private String lastName;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
