package projetojava.donodonegocio.mapper;

import projetojava.donodonegocio.dto.ClienteDTO;
import projetojava.donodonegocio.model.Cliente;
import projetojava.donodonegocio.model.Empresa;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {

    public Cliente toEntity(ClienteDTO dto) {
        if (dto == null) {
            return null;
        }
        Cliente entity = new Cliente();
        entity.setIdLocalEmpresa(dto.getIdLocalEmpresa() != null ? dto.getIdLocalEmpresa().intValue() : null);
        entity.setNome(dto.getNome());
        entity.setEndereco(dto.getEndereco());
        entity.setTelefone(dto.getTelefone());
        entity.setSaldo(dto.getSaldo());

        if (dto.getEmpresaId() != null) {
            Empresa empresa = new Empresa();
            empresa.setId(dto.getEmpresaId());
            entity.setEmpresa(empresa);
        }

        return entity;
    }

    public ClienteDTO toDTO(Cliente entity) {
        if (entity == null) {
            return null;
        }
        ClienteDTO dto = new ClienteDTO();
        dto.setEmpresaId(entity.getEmpresa() != null ? entity.getEmpresa().getId() : null);
        dto.setIdLocalEmpresa(entity.getIdLocalEmpresa() != null ? entity.getIdLocalEmpresa().longValue() : null);
        dto.setNome(entity.getNome());
        dto.setEndereco(entity.getEndereco());
        dto.setTelefone(entity.getTelefone());
        dto.setSaldo(entity.getSaldo());
        return dto;
    }

    public void updateEntityFromDTO(ClienteDTO dto, Cliente entity) {
        if (dto == null || entity == null) {
            return;
        }
        // Não existem planos para permitir alteração de idLocalEmpresa, mas se for implementado:
        if (dto.getIdLocalEmpresa() != null) {
            entity.setIdLocalEmpresa(dto.getIdLocalEmpresa().intValue());
        }
        entity.setNome(dto.getNome());
        entity.setEndereco(dto.getEndereco());
        entity.setTelefone(dto.getTelefone());
        entity.setSaldo(dto.getSaldo());
        
        // Empresa geralmente não muda, mas se necessário:
        if (dto.getEmpresaId() != null) {
            Empresa empresa = new Empresa();
            empresa.setId(dto.getEmpresaId());
            entity.setEmpresa(empresa);
        }
    }
}