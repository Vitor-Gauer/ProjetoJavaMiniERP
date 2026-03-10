package projetojava.donodonegocio.service;

import projetojava.donodonegocio.model.Empresa;
import projetojava.donodonegocio.repository.EmpresaRepository;
import projetojava.donodonegocio.repository.UsuarioRepository;
import projetojava.donodonegocio.security.AuthResponse;
import projetojava.donodonegocio.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    private final LoginAttemptService loginAttemptService;
    private final JwtService jwtService;

    public Optional<AuthResponse> authenticateEmpresa(String login, String senha, HttpServletResponse response) {
        String rateLimitKey = "empresa:" + login;

        if (loginAttemptService.isBlocked(rateLimitKey)) {
            return Optional.empty();
        }

        Optional<AuthResponse> authResponse = authenticateEmpresaComoMaster(login, senha)
                .or(() -> authenticateEmpresaComoPublico(login, senha));

        if (authResponse.isPresent()) {
            loginAttemptService.loginSucceeded(rateLimitKey);
            
            // Generate JWT token and set cookie
            CustomUserDetails userDetails = authResponse.get().userDetails();
            String jwt = jwtService.generateToken(
                userDetails.getUsername(), 
                userDetails.getEmpresaId(), 
                userDetails.getUsuarioId(), 
                userDetails.getAuthorities().iterator().next().getAuthority()
            );
            
            setJwtCookie(response, jwt);
            
            // Set authentication in context
            definirAutenticacaoNoContexto(userDetails);
        } else {
            loginAttemptService.loginFailed(rateLimitKey);
            dispararEventoFalha(login);
        }
        return authResponse;
    }

    private Optional<AuthResponse> authenticateEmpresaComoMaster(String login, String senha) {
        return empresaRepository.findByLoginMaster(login)
                .filter(empresa -> passwordEncoder.matches(senha, empresa.getSenhaHashAdmin()))
                .map(empresa -> new AuthResponse(new CustomUserDetails(empresa), null, true));
    }

    private Optional<AuthResponse> authenticateEmpresaComoPublico(String login, String senha) {
        return empresaRepository.findByLoginPublico(login)
                .filter(empresa -> passwordEncoder.matches(senha, empresa.getSenhaHashPublica()))
                .map(empresa -> new AuthResponse(new CustomUserDetails(empresa), empresa.getNome(), false));
    }

    public Optional<CustomUserDetails> authenticateUsuario(String login, String senha, Long empresaId, HttpServletResponse response) {
        String rateLimitKey = "usuario:" + empresaId + ":" + login;

        if (loginAttemptService.isBlocked(rateLimitKey)) {
            return Optional.empty();
        }

        Optional<CustomUserDetails> userDetails = authenticateUsuarioNormal(login, senha, empresaId);

        if (userDetails.isPresent()) {
            loginAttemptService.loginSucceeded(rateLimitKey);
            
            // Generate JWT token and set cookie
            CustomUserDetails user = userDetails.get();
            String jwt = jwtService.generateToken(
                user.getUsername(), 
                user.getEmpresaId(), 
                user.getUsuarioId(), 
                user.getAuthorities().iterator().next().getAuthority()
            );
            
            setJwtCookie(response, jwt);
            
            // Set authentication in context
            definirAutenticacaoNoContexto(user);
        } else {
            loginAttemptService.loginFailed(rateLimitKey);
            String prefixoFalha = empresaRepository.findById(empresaId)
                    .map(Empresa::getLoginPublico)
                    .orElse("UNKNOWN");
            dispararEventoFalha(prefixoFalha + "/" + login);
        }
        return userDetails;
    }

    private Optional<CustomUserDetails> authenticateUsuarioNormal(String login, String senha, Long empresaId) {
        return usuarioRepository.findByLoginAndEmpresaId(login, empresaId)
                .filter(usuario -> passwordEncoder.matches(senha, usuario.getSenhaHash()))
                .map(CustomUserDetails::new);
    }

    public void definirAutenticacaoNoContexto(CustomUserDetails userDetails) {
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        eventPublisher.publishEvent(new AuthenticationSuccessEvent(auth));
    }

    private void dispararEventoFalha(String username) {
        Authentication auth = new UsernamePasswordAuthenticationToken(username, null);
        eventPublisher.publishEvent(new AuthenticationFailureBadCredentialsEvent(auth, new BadCredentialsException("Falha manual de autenticação")));
    }
    
    private void setJwtCookie(HttpServletResponse response, String jwt) {
        // Create HTTP-only, SameSite=Lax cookie
        String cookieHeader = String.format(
            "jwt=%s; Path=/; Max-Age=%d; HttpOnly; SameSite=Lax",
            jwt,
            24 * 60 * 60 // 24 hours in seconds
        );
        
        response.addHeader("Set-Cookie", cookieHeader);
    }
    
    public void clearJwtCookie(HttpServletResponse response) {
        String cookieHeader = "jwt=; Path=/; Max-Age=0; HttpOnly; SameSite=Lax";
        response.addHeader("Set-Cookie", cookieHeader);
    }
}