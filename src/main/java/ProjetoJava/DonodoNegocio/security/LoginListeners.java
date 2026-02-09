package ProjetoJava.DonodoNegocio.security;

import ProjetoJava.DonodoNegocio.model.Auditoria;
import ProjetoJava.DonodoNegocio.model.Empresa;
import ProjetoJava.DonodoNegocio.model.Usuario;
import ProjetoJava.DonodoNegocio.repository.AuditoriaRepository;
import ProjetoJava.DonodoNegocio.repository.EmpresaRepository;
import ProjetoJava.DonodoNegocio.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class LoginListeners {

    private final AuditoriaRepository auditoriaRepository;
    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;

    // Armazena tentativas falhas em memória (Username -> Count)
    private final Map<String, Integer> loginAttempts = new ConcurrentHashMap<>();

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) principal;
            String username = userDetails.getUsername();
            
            // Reset tentativas falhas
            loginAttempts.remove(username);

            // Registrar Auditoria de Login
            Auditoria auditoria = new Auditoria();
            auditoria.setEmpresa(userDetails.getEmpresaEntity());
            auditoria.setLoginId(userDetails.getId());
            auditoria.setEhAdmin(userDetails.isEmpresa());
            auditoria.setTipoOperacao("LOGIN");
            auditoria.setTabelaAfetada("SISTEMA");
            auditoria.setDataHora(LocalDateTime.now());
            
            // Para login, idLocalEmpresa pode ser 0 ou null? 
            // Auditoria requer idLocalEmpresa (Integer). 
            // Se for Empresa, não tem idLocalEmpresa. Se for Usuario, tem.
            // Mas Auditoria.idLocalEmpresa é Integer.
            // Vou usar 0 para eventos de sistema/login se não houver contexto local claro.
            if (userDetails.getUsuarioEntity() != null && userDetails.getUsuarioEntity().getIdLocalEmpresa() != null) {
                auditoria.setIdLocalEmpresa(userDetails.getUsuarioEntity().getIdLocalEmpresa());
            } else {
                auditoria.setIdLocalEmpresa(0); 
            }

            auditoriaRepository.save(auditoria);
        }
    }

    @EventListener
    public void onFailure(AuthenticationFailureBadCredentialsEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        String username = principal.toString();
        
        int attempts = loginAttempts.getOrDefault(username, 0) + 1;
        loginAttempts.put(username, attempts);

        if (attempts >= 10) {
            // Tenta identificar a empresa para registrar a auditoria
            Empresa empresaAlvo = null;
            Long loginId = null;
            boolean ehAdmin = false;
            Integer idLocal = 0;

            Optional<Empresa> empOpt = empresaRepository.findByLoginMaster(username);
            if (empOpt.isPresent()) {
                empresaAlvo = empOpt.get();
                loginId = empresaAlvo.getId();
                ehAdmin = true;
            } else {
                Optional<Usuario> userOpt = usuarioRepository.findByLogin(username);
                if (userOpt.isPresent()) {
                    Usuario u = userOpt.get();
                    empresaAlvo = u.getEmpresa();
                    loginId = u.getId();
                    ehAdmin = false;
                    idLocal = u.getIdLocalEmpresa();
                }
            }

            if (empresaAlvo != null) {
                Auditoria auditoria = new Auditoria();
                auditoria.setEmpresa(empresaAlvo);
                auditoria.setLoginId(loginId);
                auditoria.setEhAdmin(ehAdmin);
                auditoria.setIdLocalEmpresa(idLocal);
                auditoria.setTipoOperacao("POSSIVEL_TENTATIVA_INVASAO");
                auditoria.setTabelaAfetada("SISTEMA");
                auditoria.setDataHora(LocalDateTime.now());
                auditoriaRepository.save(auditoria);
                
                // Reset após registrar para não floodar o banco a cada nova tentativa?
                // Ou mantém para registrar a cada 10?
                // Vou resetar para registrar novamente apenas após mais 10.
                loginAttempts.remove(username);
            }
        }
    }
}