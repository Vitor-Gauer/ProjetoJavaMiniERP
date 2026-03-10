package projetojava.donodonegocio.mapper;

import projetojava.donodonegocio.dto.TransacaoDTO;
import projetojava.donodonegocio.model.Empresa;
import projetojava.donodonegocio.model.Transacao;
import projetojava.donodonegocio.repository.TipoTransacaoRepository;
import projetojava.donodonegocio.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransacaoMapper {

    private final UsuarioRepository usuarioRepository;
    private final TipoTransacaoRepository tipoTransacaoRepository;

    public Transacao toEntity(TransacaoDTO dto) {
        if (dto.getEmpresaId() == null) {
            throw new IllegalArgumentException("Empresa ID falta na request.");
        }
        Transacao entity = new Transacao();
        if (dto.getIdLocalEmpresa() != null) {
            entity.setIdLocalEmpresa(dto.getIdLocalEmpresa().intValue());
        }
        entity.setTabelaResponsavel(dto.getTabelaResponsavel());
        entity.setResponsavelId(dto.getResponsavelId());
        entity.setGrupoId(dto.getGrupoId());
        entity.setFoiResolvido(dto.isFoiResolvido());
        entity.setEhValida(dto.isEhValida());
        if (dto.getDataCriacao() != null) {
            entity.setDataCriacao(dto.getDataCriacao());
        }
        entity.setIntervaloCobranca(dto.getIntervaloCobranca());
        entity.setDataResolucao(dto.getDataResolucao());

        Empresa empresa = createEmpresaEntity(dto.getEmpresaId());
        entity.setEmpresa(empresa);

        if (dto.getUsuarioId() != null) {
            usuarioRepository.findByEmpresaIdAndIdLocalEmpresa(dto.getEmpresaId(), dto.getUsuarioId().intValue())
                    .ifPresent(entity::setUsuario);
        }
        if (dto.getTipoId() != null) {
            tipoTransacaoRepository.findByEmpresaIdAndIdLocalEmpresa(dto.getEmpresaId(), dto.getTipoId().intValue())
                    .ifPresent(entity::setTipoTransacao);
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
        dto.setGrupoId(entity.getGrupoId());
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
        entity.setGrupoId(dto.getGrupoId());
        entity.setFoiResolvido(dto.isFoiResolvido());
        entity.setEhValida(dto.isEhValida());
        entity.setIntervaloCobranca(dto.getIntervaloCobranca());
        entity.setDataResolucao(dto.getDataResolucao());
        
        if (dto.getEmpresaId() != null) {
            updateEmpresaAndRelatedEntities(dto, entity);
        }
    }
    
    private void updateEmpresaAndRelatedEntities(TransacaoDTO dto, Transacao entity) {
        if (entity.getEmpresa() == null || !entity.getEmpresa().getId().equals(dto.getEmpresaId())) {
            Empresa empresa = createEmpresaEntity(dto.getEmpresaId());
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
    
    private Empresa createEmpresaEntity(Long empresaId) {
        Empresa empresa = new Empresa();
        empresa.setId(empresaId);
        return empresa;
    }
}