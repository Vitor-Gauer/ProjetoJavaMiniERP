package ProjetoJava.DonodoNegocio.mapper;

import ProjetoJava.DonodoNegocio.dto.TesouroDTO;
import ProjetoJava.DonodoNegocio.model.Empresa;
import ProjetoJava.DonodoNegocio.model.Tesouro;
import org.springframework.stereotype.Component;

@Component
public class TesouroMapper {

    public Tesouro toEntity(TesouroDTO dto) {
        if (dto == null) {
            return null;
        }
        Tesouro entity = new Tesouro();
        entity.setIdLocalEmpresa(dto.getIdLocalEmpresa() != null ? dto.getIdLocalEmpresa().intValue() : null);
        entity.setNomeConta(dto.getNomeConta());
        entity.setSaldoAtual(dto.getSaldoAtual());

        if (dto.getEmpresaId() != null) {
            Empresa empresa = new Empresa();
            empresa.setId(dto.getEmpresaId());
            entity.setEmpresa(empresa);
        }

        return entity;
    }

    public TesouroDTO toDTO(Tesouro entity) {
        if (entity == null) {
            return null;
        }
        TesouroDTO dto = new TesouroDTO();
        dto.setEmpresaId(entity.getEmpresa() != null ? entity.getEmpresa().getId() : null);
        dto.setIdLocalEmpresa(entity.getIdLocalEmpresa() != null ? entity.getIdLocalEmpresa().longValue() : null);
        dto.setNomeConta(entity.getNomeConta());
        dto.setSaldoAtual(entity.getSaldoAtual());
        return dto;
    }

    public void updateEntityFromDTO(TesouroDTO dto, Tesouro entity) {
        if (dto == null || entity == null) {
            return;
        }
        if (dto.getIdLocalEmpresa() != null) {
            entity.setIdLocalEmpresa(dto.getIdLocalEmpresa().intValue());
        }
        entity.setNomeConta(dto.getNomeConta());
        entity.setSaldoAtual(dto.getSaldoAtual());
        
        if (dto.getEmpresaId() != null) {
            Empresa empresa = new Empresa();
            empresa.setId(dto.getEmpresaId());
            entity.setEmpresa(empresa);
        }
    }
}