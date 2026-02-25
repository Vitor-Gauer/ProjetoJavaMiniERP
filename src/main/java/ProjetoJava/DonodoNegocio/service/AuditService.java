package ProjetoJava.DonodoNegocio.service;

import ProjetoJava.DonodoNegocio.model.Auditoria;
import ProjetoJava.DonodoNegocio.model.Empresa;
import ProjetoJava.DonodoNegocio.repository.AuditoriaRepository;
import ProjetoJava.DonodoNegocio.repository.EmpresaRepository;
import ProjetoJava.DonodoNegocio.repository.UsuarioRepository;
import ProjetoJava.DonodoNegocio.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuditService {

    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);
    private final AuditoriaRepository auditoriaRepository;
    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;

    public void logLoginSuccess(CustomUserDetails userDetails) {
        try {
            Auditoria auditoria = new Auditoria();
            Empresa empresaStub = new Empresa();
            empresaStub.setId(userDetails.getEmpresaId());

            auditoria.setEmpresa(empresaStub);
            auditoria.setLoginId(userDetails.getId());
            auditoria.setEhAdmin(userDetails.isEmpresa());
            auditoria.setIdLocalEmpresa(userDetails.getIdLocalEmpresa() != null ? userDetails.getIdLocalEmpresa() : 0);
            auditoria.setTipoOperacao("LOGIN");
            auditoria.setTabelaAfetada("SISTEMA");
            auditoria.setDataHora(LocalDateTime.now());

            auditoriaRepository.save(auditoria);
        } catch (Exception ex) {
            logger.error("Erro ao salvar auditoria de login bem-sucedido", ex);
        }
    }

    public void logInvasionAttempt(String rawUsername) {
        String[] parts = rawUsername.split("/");
        if (parts.length > 2) return;

        String loginPublicoEmpresaPart = parts[0];
        String loginUsuarioPart = parts.length == 2 ? parts[1] : null;

        Optional<Empresa> empOpt = resolveEmpresa(loginPublicoEmpresaPart, parts.length);
        
        empOpt.ifPresent(empresa -> {
            TargetInfo target = resolveTarget(empresa, loginUsuarioPart);
            try {
                Auditoria auditoria = new Auditoria();
                auditoria.setEmpresa(empresa);
                auditoria.setLoginId(target.loginId());
                auditoria.setEhAdmin(target.ehAdmin());
                auditoria.setIdLocalEmpresa(target.idLocal() != null ? target.idLocal() : 0);
                auditoria.setTipoOperacao("POSSIVEL_TENTATIVA_INVASAO");
                auditoria.setTabelaAfetada("SISTEMA");
                auditoria.setDataHora(LocalDateTime.now());
                
                auditoriaRepository.save(auditoria);
            } catch (Exception e) {
                logger.error("Erro ao salvar auditoria de tentativa de invasão", e);
            }
        });
    }

    public void logError(CustomUserDetails userDetails, String errorLocation) {
        try {
            Auditoria auditoria = new Auditoria();
            Empresa empresaStub = new Empresa();
            empresaStub.setId(userDetails.getEmpresaId());
            
            auditoria.setEmpresa(empresaStub);
            auditoria.setLoginId(userDetails.getId());
            auditoria.setEhAdmin(userDetails.isEmpresa());
            auditoria.setIdLocalEmpresa(userDetails.getIdLocalEmpresa() != null ? userDetails.getIdLocalEmpresa() : 0);
            auditoria.setTipoOperacao("ERRO_SISTEMA");
            auditoria.setTabelaAfetada(errorLocation.length() > 50 ? errorLocation.substring(0, 50) : errorLocation);
            auditoria.setDataHora(LocalDateTime.now());
            
            auditoriaRepository.save(auditoria);
        } catch (Exception ex) {
            logger.error("Erro ao salvar auditoria de erro", ex);
        }
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