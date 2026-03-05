package ProjetoJava.DonodoNegocio.service;

import ProjetoJava.DonodoNegocio.model.Empresa;
import ProjetoJava.DonodoNegocio.repository.EmpresaRepository;
import ProjetoJava.DonodoNegocio.repository.UsuarioRepository;
import ProjetoJava.DonodoNegocio.security.AuthResponse;
import ProjetoJava.DonodoNegocio.security.CustomUserDetails;
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

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    private final LoginAttemptService loginAttemptService;

    public Optional<AuthResponse> authenticateEmpresa(String login, String senha) {
        String rateLimitKey = "empresa:" + login;

        if (loginAttemptService.isBlocked(rateLimitKey)) {
            return Optional.empty();
        }

        Optional<AuthResponse> response = authenticateEmpresaComoMaster(login, senha)
                .or(() -> authenticateEmpresaComoPublico(login, senha));

        if (response.isPresent()) {
            loginAttemptService.loginSucceeded(rateLimitKey);
        } else {
            loginAttemptService.loginFailed(rateLimitKey);
            dispararEventoFalha(login);
        }
        return response;
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

    public Optional<CustomUserDetails> authenticateUsuario(String login, String senha, Long empresaId) {
        String rateLimitKey = "usuario:" + empresaId + ":" + login;

        if (loginAttemptService.isBlocked(rateLimitKey)) {
            return Optional.empty();
        }

        Optional<CustomUserDetails> userDetails = authenticateUsuarioNormal(login, senha, empresaId);

        if (userDetails.isPresent()) {
            loginAttemptService.loginSucceeded(rateLimitKey);
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
}