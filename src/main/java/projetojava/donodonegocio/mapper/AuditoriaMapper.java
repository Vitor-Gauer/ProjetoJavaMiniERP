package projetojava.donodonegocio.mapper;

import projetojava.donodonegocio.dto.AuditoriaDTO;
import projetojava.donodonegocio.model.Auditoria;
import projetojava.donodonegocio.model.Empresa;
import projetojava.donodonegocio.config.AppConstants;
import org.springframework.stereotype.Component;

@Component
public class AuditoriaMapper {

    public Auditoria toEntity(AuditoriaDTO dto) {
        if (dto == null) {
            return null;
        }
        Auditoria entity = new Auditoria();
        entity.setIdLocalEmpresa(dto.getIdLocalEmpresa());
        entity.setLoginId(dto.getLoginId());
        entity.setEhAdmin(dto.isEhAdmin());
        entity.setTipoOperacao(dto.getTipoOperacao());
        entity.setTabelaAfetada(dto.getTabelaAfetada());

        if (dto.getDataHora() != null) {
            entity.setDataHora(dto.getDataHora());
        }

        if (dto.getEmpresaId() != null) {
            Empresa empresa = new Empresa();
            empresa.setId(dto.getEmpresaId());
            entity.setEmpresa(empresa);
        }

        return entity;
    }

    public AuditoriaDTO toDTO(Auditoria entity) {
        if (entity == null) {
            return null;
        }
        AuditoriaDTO dto = new AuditoriaDTO();
        dto.setEmpresaId(entity.getEmpresa() != null ? entity.getEmpresa().getId() : null);
        dto.setIdLocalEmpresa(entity.getIdLocalEmpresa());
        dto.setLoginId(entity.getLoginId());
        dto.setEhAdmin(entity.isEhAdmin());
        dto.setTipoOperacao(entity.getTipoOperacao());
        dto.setTabelaAfetada(entity.getTabelaAfetada());
        dto.setDataHora(entity.getDataHora());

        return dto;
    }

    public Auditoria createLoginAuditoria(Long empresaId, Long loginId, boolean ehAdmin, Integer idLocalEmpresa) {
        Auditoria auditoria = new Auditoria();
        auditoria.setLoginId(loginId);
        auditoria.setEhAdmin(ehAdmin);
        auditoria.setIdLocalEmpresa(idLocalEmpresa);
        auditoria.setTipoOperacao("LOGIN");
        auditoria.setTabelaAfetada(AppConstants.TABELA_SISTEMA);
        auditoria.setDataHora(java.time.LocalDateTime.now());

        projetojava.donodonegocio.model.Empresa empresa = new projetojava.donodonegocio.model.Empresa();
        empresa.setId(empresaId);
        auditoria.setEmpresa(empresa);

        return auditoria;
    }

    public Auditoria createLogoutAuditoria(Long empresaId, Long loginId, boolean ehAdmin, Integer idLocalEmpresa) {
        Auditoria auditoria = new Auditoria();
        auditoria.setLoginId(loginId);
        auditoria.setEhAdmin(ehAdmin);
        auditoria.setIdLocalEmpresa(idLocalEmpresa);
        auditoria.setTipoOperacao("LOGOUT");
        auditoria.setTabelaAfetada(AppConstants.TABELA_SISTEMA);
        auditoria.setDataHora(java.time.LocalDateTime.now());

        projetojava.donodonegocio.model.Empresa empresa = new projetojava.donodonegocio.model.Empresa();
        empresa.setId(empresaId);
        auditoria.setEmpresa(empresa);

        return auditoria;
    }

    public Auditoria createInvasionAttemptAuditoria(Long empresaId, Long loginId, boolean ehAdmin, Integer idLocalEmpresa) {
        Auditoria auditoria = new Auditoria();
        auditoria.setLoginId(loginId);
        auditoria.setEhAdmin(ehAdmin);
        auditoria.setIdLocalEmpresa(idLocalEmpresa);
        auditoria.setTipoOperacao("POSSIVEL_TENTATIVA_INVASAO");
        auditoria.setTabelaAfetada(AppConstants.TABELA_SISTEMA);
        auditoria.setDataHora(java.time.LocalDateTime.now());

        projetojava.donodonegocio.model.Empresa empresa = new projetojava.donodonegocio.model.Empresa();
        empresa.setId(empresaId);
        auditoria.setEmpresa(empresa);

        return auditoria;
    }

    public Auditoria createErrorAuditoria(Long empresaId, Long loginId, boolean ehAdmin, Integer idLocalEmpresa, String errorLocation) {
        Auditoria auditoria = new Auditoria();
        auditoria.setLoginId(loginId);
        auditoria.setEhAdmin(ehAdmin);
        auditoria.setIdLocalEmpresa(idLocalEmpresa);
        auditoria.setTipoOperacao("ERRO_SISTEMA");
        auditoria.setTabelaAfetada(errorLocation);
        auditoria.setDataHora(java.time.LocalDateTime.now());

        projetojava.donodonegocio.model.Empresa empresa = new projetojava.donodonegocio.model.Empresa();
        empresa.setId(empresaId);
        auditoria.setEmpresa(empresa);

        return auditoria;
    }
}