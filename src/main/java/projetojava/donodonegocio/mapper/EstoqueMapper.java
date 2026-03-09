package projetojava.donodonegocio.mapper;

import projetojava.donodonegocio.dto.EstoqueDTO;
import projetojava.donodonegocio.model.Empresa;
import projetojava.donodonegocio.model.Estoque;
import org.springframework.stereotype.Component;

@Component
public class EstoqueMapper {

    public Estoque toEntity(EstoqueDTO dto) {
        if (dto == null) {
            return null;
        }
        Estoque entity = new Estoque();
        entity.setIdLocalEmpresa(dto.getIdLocalEmpresa() != null ? dto.getIdLocalEmpresa().intValue() : null);
        entity.setNome(dto.getNome());

        if (dto.getEmpresaId() != null) {
            Empresa empresa = new Empresa();
            empresa.setId(dto.getEmpresaId());
            entity.setEmpresa(empresa);
        }

        return entity;
    }

    public EstoqueDTO toDTO(Estoque entity) {
        if (entity == null) {
            return null;
        }
        EstoqueDTO dto = new EstoqueDTO();
        dto.setEmpresaId(entity.getEmpresa() != null ? entity.getEmpresa().getId() : null);
        dto.setIdLocalEmpresa(entity.getIdLocalEmpresa() != null ? entity.getIdLocalEmpresa().longValue() : null);
        dto.setNome(entity.getNome());
        return dto;
    }

    public void updateEntityFromDTO(EstoqueDTO dto, Estoque entity) {
        if (dto == null || entity == null) {
            return;
        }
        if (dto.getIdLocalEmpresa() != null) {
            entity.setIdLocalEmpresa(dto.getIdLocalEmpresa().intValue());
        }
        entity.setNome(dto.getNome());
        
        if (dto.getEmpresaId() != null) {
            Empresa empresa = new Empresa();
            empresa.setId(dto.getEmpresaId());
            entity.setEmpresa(empresa);
        }
    }
}