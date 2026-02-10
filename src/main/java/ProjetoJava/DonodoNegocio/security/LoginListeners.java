package ProjetoJava.DonodoNegocio.security;

import ProjetoJava.DonodoNegocio.mapper.AuditoriaMapper;
import ProjetoJava.DonodoNegocio.model.Auditoria;
import ProjetoJava.DonodoNegocio.model.Empresa;
import ProjetoJava.DonodoNegocio.repository.AuditoriaRepository;
import ProjetoJava.DonodoNegocio.repository.EmpresaRepository;
import ProjetoJava.DonodoNegocio.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class LoginListeners {

    private static final Logger logger = LoggerFactory.getLogger(LoginListeners.class);

    private final AuditoriaRepository auditoriaRepository;
    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaMapper auditoriaMapper;

    // Armazena tentativas falhas em memória (Username -> Count)
    private final Map<String, Integer> loginAttempts = new ConcurrentHashMap<>();

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails userDetails) {
            loginAttempts.remove(userDetails.getUsername());

            try {
                Auditoria auditoria = auditoriaMapper.toEntityLogin(userDetails);
                auditoriaRepository.save(auditoria);
            } catch (Exception e) {
                logger.error("Erro ao salvar auditoria de login", e);
            }
        }
    }

    @EventListener
    public void onFailure(AuthenticationFailureBadCredentialsEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        if (principal == null) return;

        String rawUsername = principal.toString();
        
        int attempts = loginAttempts.getOrDefault(rawUsername, 0) + 1;
        loginAttempts.put(rawUsername, attempts);

        if (attempts >= 10) {
            handleInvasionAttempt(rawUsername);
        }
    }

    private void handleInvasionAttempt(String rawUsername) {
        String[] parts = rawUsername.split("/");
        String loginPublicoEmpresaPart = parts[0];
        String loginUsuarioPart = parts.length == 2 ? parts[1] : null;

        if (parts.length > 2) return;

        Optional<Empresa> empOpt = resolveEmpresa(loginPublicoEmpresaPart, parts.length);
        
        empOpt.ifPresent(empresa -> {
            TargetInfo target = resolveTarget(empresa, loginUsuarioPart);
            try {
                Auditoria auditoria = auditoriaMapper.toEntityInvasao(empresa, target.loginId(), target.ehAdmin(), target.idLocal());
                auditoriaRepository.save(auditoria);
                loginAttempts.remove(rawUsername);
            } catch (Exception e) {
                logger.error("Erro ao salvar auditoria de tentativa de invasão", e);
            }
        });
    }

    private Optional<Empresa> resolveEmpresa(String loginPart, int partsLength) {
        Optional<Empresa> empOpt = empresaRepository.findByLoginPublico(loginPart);
        if (empOpt.isEmpty() && partsLength == 1) {
            empOpt = empresaRepository.findByLoginMaster(loginPart);
        }
        return empOpt;
    }

    private TargetInfo resolveTarget(Empresa empresa, String loginUsuarioPart) {
        if (loginUsuarioPart == null) {
            return new TargetInfo(empresa.getId(), true, 0);
        }

        if (empresa.getLoginMaster().equals(loginUsuarioPart)) {
            return new TargetInfo(empresa.getId(), true, 0);
        }

        return usuarioRepository.findByLoginAndEmpresaId(loginUsuarioPart, empresa.getId())
                .map(u -> new TargetInfo(u.getId(), false, u.getIdLocalEmpresa()))
                .orElse(new TargetInfo(empresa.getId(), true, 0));
    }

    private record TargetInfo(Long loginId, boolean ehAdmin, Integer idLocal) {}
}