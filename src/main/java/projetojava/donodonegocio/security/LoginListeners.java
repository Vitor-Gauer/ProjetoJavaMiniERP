package projetojava.donodonegocio.security;

import projetojava.donodonegocio.service.AuditService;
import projetojava.donodonegocio.service.LoginAttemptService;
import projetojava.donodonegocio.service.CobrancaAvisoService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpSession;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LoginListeners {

    private final LoginAttemptService loginAttemptService;
    private final AuditService auditService;
    private final CobrancaAvisoService cobrancaAvisoService;
    private final HttpSession httpSession;

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        if (event.getAuthentication().getPrincipal() instanceof CustomUserDetails userDetails) {
            loginAttemptService.loginSucceeded(userDetails.getUsername());
            auditService.logLoginSuccess(userDetails);

            cobrancaAvisoService.marcarAvisoNaSessaoSeNecessario(userDetails, httpSession);
        }
    }

    @EventListener
    public void onFailure(AuthenticationFailureBadCredentialsEvent event) {
        Optional.of(event.getAuthentication())
                .map(Authentication::getPrincipal)
                .map(Object::toString)
                .ifPresent(username -> {
                    loginAttemptService.loginFailed(username);

                    if (loginAttemptService.isBlocked(username)) {
                        auditService.logInvasionAttempt(username);
                        loginAttemptService.unblock(username);
                    }
                });
    }
}