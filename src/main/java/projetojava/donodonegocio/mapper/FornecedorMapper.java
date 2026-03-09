package projetojava.donodonegocio.mapper;

import projetojava.donodonegocio.dto.FornecedorDTO;
import projetojava.donodonegocio.model.Empresa;
import projetojava.donodonegocio.model.Fornecedor;
import org.springframework.stereotype.Component;

@Component
public class FornecedorMapper {

    public Fornecedor toEntity(FornecedorDTO dto) {
        if (dto == null) {
            return null;
        }
        Fornecedor entity = new Fornecedor();
        entity.setIdLocalEmpresa(dto.getIdLocalEmpresa() != null ? dto.getIdLocalEmpresa().intValue() : null);
        entity.setNome(dto.getNome());
        entity.setDocumento(dto.getDocumento());

        if (dto.getEmpresaId() != null) {
            Empresa empresa = new Empresa();
            empresa.setId(dto.getEmpresaId());
            entity.setEmpresa(empresa);
        }

        return entity;
    }

    public FornecedorDTO toDTO(Fornecedor entity) {
        if (entity == null) {
            return null;
        }
        FornecedorDTO dto = new FornecedorDTO();
        dto.setEmpresaId(entity.getEmpresa() != null ? entity.getEmpresa().getId() : null);
        dto.setIdLocalEmpresa(entity.getIdLocalEmpresa() != null ? entity.getIdLocalEmpresa().longValue() : null);
        dto.setNome(entity.getNome());
        dto.setDocumento(entity.getDocumento());
        return dto;
    }

    public void updateEntityFromDTO(FornecedorDTO dto, Fornecedor entity) {
        if (dto == null || entity == null) {
            return;
        }
        if (dto.getIdLocalEmpresa() != null) {
            entity.setIdLocalEmpresa(dto.getIdLocalEmpresa().intValue());
        }
        entity.setNome(dto.getNome());
        entity.setDocumento(dto.getDocumento());
        
        if (dto.getEmpresaId() != null) {
            Empresa empresa = new Empresa();
            empresa.setId(dto.getEmpresaId());
            entity.setEmpresa(empresa);
        }
    }
}