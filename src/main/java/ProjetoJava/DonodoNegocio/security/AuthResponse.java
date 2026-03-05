package ProjetoJava.DonodoNegocio.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AuthResponse {
    private final CustomUserDetails userDetails;
    private final String empresaNome;
    private final boolean isMasterLogin;

    public AuthResponse(CustomUserDetails userDetails, boolean isMasterLogin) {
        this(userDetails, null, isMasterLogin);
    }
}