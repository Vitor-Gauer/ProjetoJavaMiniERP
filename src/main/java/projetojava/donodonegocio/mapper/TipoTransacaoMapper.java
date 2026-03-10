package projetojava.donodonegocio.mapper;

import projetojava.donodonegocio.dto.TipoTransacaoDTO;
import projetojava.donodonegocio.model.Empresa;
import projetojava.donodonegocio.model.TipoTransacao;
import org.springframework.stereotype.Component;

@Component
public class TipoTransacaoMapper {

    public TipoTransacao toEntity(TipoTransacaoDTO dto) {
        if (dto == null) {
            return null;
        }
        TipoTransacao entity = new TipoTransacao();
        entity.setIdLocalEmpresa(dto.getIdLocalEmpresa() != null ? dto.getIdLocalEmpresa().intValue() : null);
        entity.setNome(dto.getNome());
        entity.setPrctJuros(dto.getPrctJuros());
        entity.setEhRecebimento(dto.isEhRecebimento());
        entity.setEhRecorrente(dto.isEhRecorrente());

        if (dto.getEmpresaId() != null) {
            Empresa empresa = new Empresa();
            empresa.setId(dto.getEmpresaId());
            entity.setEmpresa(empresa);
        }

        return entity;
    }

    public TipoTransacaoDTO toDTO(TipoTransacao entity) {
        if (entity == null) {
            return null;
        }
        TipoTransacaoDTO dto = new TipoTransacaoDTO();
        dto.setEmpresaId(entity.getEmpresa() != null ? entity.getEmpresa().getId() : null);
        dto.setIdLocalEmpresa(entity.getIdLocalEmpresa() != null ? entity.getIdLocalEmpresa().longValue() : null);
        dto.setNome(entity.getNome());
        dto.setPrctJuros(entity.getPrctJuros());
        dto.setEhRecebimento(entity.isEhRecebimento());
        dto.setEhRecorrente(entity.isEhRecorrente());
        return dto;
    }

    public void updateEntityFromDTO(TipoTransacaoDTO dto, TipoTransacao entity) {
        if (dto == null || entity == null) {
            return;
        }
        if (dto.getIdLocalEmpresa() != null) {
            entity.setIdLocalEmpresa(dto.getIdLocalEmpresa().intValue());
        }
        entity.setNome(dto.getNome());
        entity.setPrctJuros(dto.getPrctJuros());
        entity.setEhRecebimento(dto.isEhRecebimento());
        entity.setEhRecorrente(dto.isEhRecorrente());
        
        if (dto.getEmpresaId() != null) {
            Empresa empresa = new Empresa();
            empresa.setId(dto.getEmpresaId());
            entity.setEmpresa(empresa);
        }
    }
}