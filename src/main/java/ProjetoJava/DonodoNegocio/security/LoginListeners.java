package ProjetoJava.DonodoNegocio.security;

import ProjetoJava.DonodoNegocio.service.AuditService;
import ProjetoJava.DonodoNegocio.service.LoginAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LoginListeners {

    private final LoginAttemptService loginAttemptService;
    private final AuditService auditService;

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        if (event.getAuthentication().getPrincipal() instanceof CustomUserDetails userDetails) {
            loginAttemptService.loginSucceeded(userDetails.getUsername());
            auditService.logLoginSuccess(userDetails);
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