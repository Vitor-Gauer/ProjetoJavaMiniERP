package ProjetoJava.DonodoNegocio.mapper;

import ProjetoJava.DonodoNegocio.dto.MovimentacaoDTO;
import ProjetoJava.DonodoNegocio.model.Empresa;
import ProjetoJava.DonodoNegocio.model.Movimentacao;
import ProjetoJava.DonodoNegocio.repository.TransacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MovimentacaoMapper {

    private final TransacaoRepository transacaoRepository;

    public Movimentacao toEntity(MovimentacaoDTO dto) {
        if (dto == null) {
            return null;
        }
        Movimentacao entity = new Movimentacao();
        entity.setIdLocalEmpresa(dto.getIdLocalEmpresa() != null ? dto.getIdLocalEmpresa().intValue() : null);
        entity.setTabelaMovimentada(dto.getTabelaMovimentada());
        entity.setMovimentadoId(dto.getMovimentadoId());
        entity.setQuantidade(dto.getQuantidade());
        entity.setEhEntrada(dto.isEhEntrada());
        entity.setFoiResolvida(dto.isFoiResolvida());

        if (dto.getEmpresaId() != null) {
            Empresa empresa = new Empresa();
            empresa.setId(dto.getEmpresaId());
            entity.setEmpresa(empresa);

            if (dto.getTransacaoPaiId() != null) {
                transacaoRepository.findByEmpresaIdAndIdLocalEmpresa(dto.getEmpresaId(), dto.getTransacaoPaiId().intValue())
                        .ifPresent(entity::setTransacaoPai);
            }
        }

        return entity;
    }

    public MovimentacaoDTO toDTO(Movimentacao entity) {
        if (entity == null) {
            return null;
        }
        MovimentacaoDTO dto = new MovimentacaoDTO();
        dto.setEmpresaId(entity.getEmpresa() != null ? entity.getEmpresa().getId() : null);
        dto.setIdLocalEmpresa(entity.getIdLocalEmpresa() != null ? entity.getIdLocalEmpresa().longValue() : null);
        dto.setTabelaMovimentada(entity.getTabelaMovimentada());
        dto.setMovimentadoId(entity.getMovimentadoId());
        dto.setQuantidade(entity.getQuantidade());
        dto.setEhEntrada(entity.isEhEntrada());
        dto.setFoiResolvida(entity.isFoiResolvida());

        if (entity.getTransacaoPai() != null && entity.getTransacaoPai().getIdLocalEmpresa() != null) {
            dto.setTransacaoPaiId(entity.getTransacaoPai().getIdLocalEmpresa().longValue());
        }

        return dto;
    }

    public void updateEntityFromDTO(MovimentacaoDTO dto, Movimentacao entity) {
        if (dto == null || entity == null) {
            return;
        }
        if (dto.getIdLocalEmpresa() != null) {
            entity.setIdLocalEmpresa(dto.getIdLocalEmpresa().intValue());
        }
        entity.setTabelaMovimentada(dto.getTabelaMovimentada());
        entity.setMovimentadoId(dto.getMovimentadoId());
        entity.setQuantidade(dto.getQuantidade());
        entity.setEhEntrada(dto.isEhEntrada());
        entity.setFoiResolvida(dto.isFoiResolvida());
        
        if (dto.getEmpresaId() != null) {
            if (entity.getEmpresa() == null || !entity.getEmpresa().getId().equals(dto.getEmpresaId())) {
                Empresa empresa = new Empresa();
                empresa.setId(dto.getEmpresaId());
                entity.setEmpresa(empresa);
            }

            if (dto.getTransacaoPaiId() != null) {
                transacaoRepository.findByEmpresaIdAndIdLocalEmpresa(dto.getEmpresaId(), dto.getTransacaoPaiId().intValue())
                        .ifPresent(entity::setTransacaoPai);
            }
        }
    }
}