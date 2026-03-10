package projetojava.donodonegocio.service;

import projetojava.donodonegocio.mapper.AuditoriaMapper;
import projetojava.donodonegocio.model.Auditoria;
import projetojava.donodonegocio.model.Empresa;
import projetojava.donodonegocio.repository.AuditoriaRepository;
import projetojava.donodonegocio.repository.EmpresaRepository;
import projetojava.donodonegocio.repository.UsuarioRepository;
import projetojava.donodonegocio.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuditService {

    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);
    private final AuditoriaRepository auditoriaRepository;
    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaMapper auditoriaMapper;

    public void logLoginSuccess(CustomUserDetails userDetails) {
        try {
            Auditoria auditoria = auditoriaMapper.createLoginAuditoria(
                    userDetails.getEmpresaId(),
                    userDetails.getId(),
                    userDetails.isEmpresa(),
                    userDetails.getIdLocalEmpresa() != null ? userDetails.getIdLocalEmpresa() : 0
            );
            auditoriaRepository.save(auditoria);
        } catch (Exception ex) {
            logger.error("Erro ao salvar auditoria de login bem-sucedido", ex);
        }
    }

    public void logLogout(CustomUserDetails userDetails) {
        try {
            Auditoria auditoria = auditoriaMapper.createLogoutAuditoria(
                    userDetails.getEmpresaId(),
                    userDetails.getId(),
                    userDetails.isEmpresa(),
                    userDetails.getIdLocalEmpresa() != null ? userDetails.getIdLocalEmpresa() : 0
            );
            auditoriaRepository.save(auditoria);
        } catch (Exception ex) {
            logger.error("Erro ao salvar auditoria de logout", ex);
        }
    }

    public void logInvasionAttempt(String rawUsername) {
        String[] parts = rawUsername.split("/", 2);

        String loginPublicoEmpresaPart = parts[0];
        String loginUsuarioPart = parts.length == 2 ? parts[1] : null;

        Optional<Empresa> empOpt = resolveEmpresa(loginPublicoEmpresaPart, parts.length);

        empOpt.ifPresent(empresa -> {
            TargetInfo target = resolveTarget(empresa, loginUsuarioPart);
            try {
                Auditoria auditoria = auditoriaMapper.createInvasionAttemptAuditoria(
                        empresa.getId(),
                        target.loginId(),
                        target.ehAdmin(),
                        target.idLocal() != null ? target.idLocal() : 0
                );
                auditoriaRepository.save(auditoria);
            } catch (Exception e) {
                logger.error("Erro ao salvar auditoria de tentativa de invasão", e);
            }
        });
    }

    public void logError(CustomUserDetails userDetails, String errorLocation) {
        try {
            Auditoria auditoria = auditoriaMapper.createErrorAuditoria(
                    userDetails.getEmpresaId(),
                    userDetails.getId(),
                    userDetails.isEmpresa(),
                    userDetails.getIdLocalEmpresa() != null ? userDetails.getIdLocalEmpresa() : 0,
                    errorLocation.length() > 50 ? errorLocation.substring(0, 50) : errorLocation
            );
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

    private record TargetInfo(Long loginId, boolean ehAdmin, Integer idLocal) {
    }
}