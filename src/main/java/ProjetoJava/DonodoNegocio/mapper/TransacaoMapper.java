package ProjetoJava.DonodoNegocio.mapper;

import ProjetoJava.DonodoNegocio.dto.TransacaoDTO;
import ProjetoJava.DonodoNegocio.model.Empresa;
import ProjetoJava.DonodoNegocio.model.Transacao;
import ProjetoJava.DonodoNegocio.repository.TipoTransacaoRepository;
import ProjetoJava.DonodoNegocio.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransacaoMapper {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TipoTransacaoRepository tipoTransacaoRepository;

    public Transacao toEntity(TransacaoDTO dto) {
        if (dto == null) {
            return null;
        }
        Transacao entity = new Transacao();
        entity.setIdLocalEmpresa(dto.getIdLocalEmpresa() != null ? dto.getIdLocalEmpresa().intValue() : null);
        entity.setTabelaResponsavel(dto.getTabelaResponsavel());
        entity.setResponsavelId(dto.getResponsavelId());
        entity.setFoiResolvido(dto.isFoiResolvido());
        entity.setEhValida(dto.isEhValida());
        if (dto.getDataCriacao() != null) {
            entity.setDataCriacao(dto.getDataCriacao());
        }
        entity.setIntervaloCobranca(dto.getIntervaloCobranca());
        entity.setDataResolucao(dto.getDataResolucao());

        if (dto.getEmpresaId() != null) {
            Empresa empresa = new Empresa();
            empresa.setId(dto.getEmpresaId());
            entity.setEmpresa(empresa);

            if (dto.getUsuarioId() != null) {
                usuarioRepository.findByEmpresaIdAndIdLocalEmpresa(dto.getEmpresaId(), dto.getUsuarioId().intValue())
                        .ifPresent(entity::setUsuario);
            }
            if (dto.getTipoId() != null) {
                tipoTransacaoRepository.findByEmpresaIdAndIdLocalEmpresa(dto.getEmpresaId(), dto.getTipoId().intValue())
                        .ifPresent(entity::setTipoTransacao);
            }
        }

        return entity;
    }

    public TransacaoDTO toDTO(Transacao entity) {
        if (entity == null) {
            return null;
        }
        TransacaoDTO dto = new TransacaoDTO();
        dto.setEmpresaId(entity.getEmpresa() != null ? entity.getEmpresa().getId() : null);
        dto.setIdLocalEmpresa(entity.getIdLocalEmpresa() != null ? entity.getIdLocalEmpresa().longValue() : null);
        dto.setTabelaResponsavel(entity.getTabelaResponsavel());
        dto.setResponsavelId(entity.getResponsavelId());
        dto.setFoiResolvido(entity.isFoiResolvido());
        dto.setEhValida(entity.isEhValida());
        dto.setDataCriacao(entity.getDataCriacao());
        dto.setIntervaloCobranca(entity.getIntervaloCobranca());
        dto.setDataResolucao(entity.getDataResolucao());

        if (entity.getUsuario() != null && entity.getUsuario().getIdLocalEmpresa() != null) {
            dto.setUsuarioId(entity.getUsuario().getIdLocalEmpresa().longValue());
        }
        if (entity.getTipoTransacao() != null && entity.getTipoTransacao().getIdLocalEmpresa() != null) {
            dto.setTipoId(entity.getTipoTransacao().getIdLocalEmpresa().longValue());
        }

        return dto;
    }

    public void updateEntityFromDTO(TransacaoDTO dto, Transacao entity) {
        if (dto == null || entity == null) {
            return;
        }
        if (dto.getIdLocalEmpresa() != null) {
            entity.setIdLocalEmpresa(dto.getIdLocalEmpresa().intValue());
        }
        entity.setTabelaResponsavel(dto.getTabelaResponsavel());
        entity.setResponsavelId(dto.getResponsavelId());
        entity.setFoiResolvido(dto.isFoiResolvido());
        entity.setEhValida(dto.isEhValida());
        entity.setIntervaloCobranca(dto.getIntervaloCobranca());
        entity.setDataResolucao(dto.getDataResolucao());
        
        if (dto.getEmpresaId() != null) {
            if (entity.getEmpresa() == null || !entity.getEmpresa().getId().equals(dto.getEmpresaId())) {
                Empresa empresa = new Empresa();
                empresa.setId(dto.getEmpresaId());
                entity.setEmpresa(empresa);
            }

            if (dto.getUsuarioId() != null) {
                usuarioRepository.findByEmpresaIdAndIdLocalEmpresa(dto.getEmpresaId(), dto.getUsuarioId().intValue())
                        .ifPresent(entity::setUsuario);
            }
            if (dto.getTipoId() != null) {
                tipoTransacaoRepository.findByEmpresaIdAndIdLocalEmpresa(dto.getEmpresaId(), dto.getTipoId().intValue())
                        .ifPresent(entity::setTipoTransacao);
            }
        }
    }
}