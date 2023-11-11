package mjr.personalfinance.api.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import mjr.personalfinance.api.user.Role;
import mjr.personalfinance.api.user.User;
import mjr.personalfinance.api.user.UserRepository;

@Service
@Transactional
@AllArgsConstructor
public class AuthenticationService {

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    private AuthenticationManager authenticationManager;

    private TokenService tokenService;

    public AuthenticationResponseDTO register(RegistrationRequestDTO request) {
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);
        String jwtToken = tokenService.generateJwt(savedUser);
        return AuthenticationResponseDTO.builder().accessToken(jwtToken).build();
    }

    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request) {
        try {
            authenticationManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            User user = userRepository.findUserByUsername(request.getUsername()).orElseThrow();

            String token = tokenService.generateJwt(user);
            return new AuthenticationResponseDTO(token);
        } catch (AuthenticationException e) {
            return new AuthenticationResponseDTO("");
        }
    }

}
