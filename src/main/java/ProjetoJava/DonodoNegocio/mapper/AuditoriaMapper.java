package ProjetoJava.DonodoNegocio.mapper;

import ProjetoJava.DonodoNegocio.dto.AuditoriaDTO;
import ProjetoJava.DonodoNegocio.model.Auditoria;
import ProjetoJava.DonodoNegocio.model.Empresa;
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
}