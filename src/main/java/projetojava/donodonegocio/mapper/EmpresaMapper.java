package projetojava.donodonegocio.mapper;

import projetojava.donodonegocio.dto.EmpresaDTO;
import projetojava.donodonegocio.model.Empresa;
import org.springframework.stereotype.Component;

@Component
public class EmpresaMapper {

    public Empresa toEntity(EmpresaDTO dto) {
        if (dto == null) {
            return null;
        }
        Empresa entity = new Empresa();
        entity.setNome(dto.getNome());
        entity.setLoginMaster(dto.getLoginMaster());
        entity.setSenhaHashAdmin(dto.getSenhaAdmin());
        entity.setSenhaHashPublica(dto.getSenhaPublica());
        
        return entity;
    }

    public EmpresaDTO toDTO(Empresa entity) {
        if (entity == null) {
            return null;
        }
        EmpresaDTO dto = new EmpresaDTO();
        dto.setNome(entity.getNome());
        dto.setLoginMaster(entity.getLoginMaster());
        return dto;
    }

    public void updateEntityFromDTO(EmpresaDTO dto, Empresa entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setNome(dto.getNome());
        entity.setLoginMaster(dto.getLoginMaster());
        
        if (dto.getSenhaAdmin() != null) {
            entity.setSenhaHashAdmin(dto.getSenhaAdmin());
        }
        if (dto.getSenhaPublica() != null) {
            entity.setSenhaHashPublica(dto.getSenhaPublica());
        }
    }
}