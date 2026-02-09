package ProjetoJava.DonodoNegocio.mapper;

import ProjetoJava.DonodoNegocio.dto.TipoUsuarioDTO;
import ProjetoJava.DonodoNegocio.model.Empresa;
import ProjetoJava.DonodoNegocio.model.TipoUsuario;
import org.springframework.stereotype.Component;

@Component
public class TipoUsuarioMapper {

    public TipoUsuario toEntity(TipoUsuarioDTO dto) {
        if (dto == null) {
            return null;
        }
        TipoUsuario entity = new TipoUsuario();
        entity.setIdLocalEmpresa(dto.getIdLocalEmpresa() != null ? dto.getIdLocalEmpresa().intValue() : null);
        entity.setCargo(dto.getCargo());

        if (dto.getEmpresaId() != null) {
            Empresa empresa = new Empresa();
            empresa.setId(dto.getEmpresaId());
            entity.setEmpresa(empresa);
        }

        return entity;
    }

    public TipoUsuarioDTO toDTO(TipoUsuario entity) {
        if (entity == null) {
            return null;
        }
        TipoUsuarioDTO dto = new TipoUsuarioDTO();
        dto.setEmpresaId(entity.getEmpresa() != null ? entity.getEmpresa().getId() : null);
        dto.setIdLocalEmpresa(entity.getIdLocalEmpresa() != null ? entity.getIdLocalEmpresa().longValue() : null);
        dto.setCargo(entity.getCargo());
        return dto;
    }

    public void updateEntityFromDTO(TipoUsuarioDTO dto, TipoUsuario entity) {
        if (dto == null || entity == null) {
            return;
        }
        if (dto.getIdLocalEmpresa() != null) {
            entity.setIdLocalEmpresa(dto.getIdLocalEmpresa().intValue());
        }
        entity.setCargo(dto.getCargo());
        
        if (dto.getEmpresaId() != null) {
            Empresa empresa = new Empresa();
            empresa.setId(dto.getEmpresaId());
            entity.setEmpresa(empresa);
        }
    }
}