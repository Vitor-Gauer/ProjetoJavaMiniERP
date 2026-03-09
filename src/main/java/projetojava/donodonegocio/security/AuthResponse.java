package projetojava.donodonegocio.security;

public record AuthResponse(CustomUserDetails userDetails, String empresaNome, boolean isMasterLogin) {
    public AuthResponse(CustomUserDetails userDetails, boolean isMasterLogin) {
        this(userDetails, null, isMasterLogin);
    }
}