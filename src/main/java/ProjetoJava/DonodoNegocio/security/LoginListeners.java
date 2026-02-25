package ProjetoJava.DonodoNegocio.security;

import ProjetoJava.DonodoNegocio.service.AuditService;
import ProjetoJava.DonodoNegocio.service.LoginAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginListeners {

    private final LoginAttemptService loginAttemptService;
    private final AuditService auditService;

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        if (event.getAuthentication() != null && event.getAuthentication().getPrincipal() instanceof CustomUserDetails userDetails) {
            loginAttemptService.loginSucceeded(userDetails.getUsername());
            auditService.logLoginSuccess(userDetails);
        }
    }

    @EventListener
    public void onFailure(AuthenticationFailureBadCredentialsEvent event) {
        if (event.getAuthentication() != null && event.getAuthentication().getPrincipal() != null) {
            String username = event.getAuthentication().getPrincipal().toString();
            loginAttemptService.loginFailed(username);

            if (loginAttemptService.isBlocked(username)) {
                auditService.logInvasionAttempt(username);
                loginAttemptService.unblock(username); // Reset after logging
            }
        }
    }
}