package ProjetoJava.DonodoNegocio.mapper;

import ProjetoJava.DonodoNegocio.dto.AuditoriaDTO;
import ProjetoJava.DonodoNegocio.model.Auditoria;
import ProjetoJava.DonodoNegocio.model.Empresa;
import ProjetoJava.DonodoNegocio.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuditoriaMapper {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Auditoria toEntity(AuditoriaDTO dto) {
        if (dto == null) {
            return null;
        }
        Auditoria entity = new Auditoria();
        entity.setIdLocalEmpresa(dto.getIdLocalEmpresa());
        entity.setTipoOperacao(dto.getTipoOperacao());
        entity.setTabelaAfetada(dto.getTabelaAfetada());
        // dataHora é gerada no @PrePersist, mas se vier no DTO (ex: migração), pode setar
        if (dto.getDataHora() != null) {
            entity.setDataHora(dto.getDataHora());
        }

        if (dto.getEmpresaId() != null) {
            Empresa empresa = new Empresa();
            empresa.setId(dto.getEmpresaId());
            entity.setEmpresa(empresa);

            if (dto.getUsuarioId() != null) {
                // usuarioId no DTO provavelmente é o local ID, pois UsuarioDTO retorna local ID
                usuarioRepository.findByEmpresaIdAndIdLocalEmpresa(dto.getEmpresaId(), dto.getUsuarioId().intValue())
                        .ifPresent(entity::setUsuario);
            }
        }

        return entity;
    }

    public AuditoriaDTO toDTO(Auditoria entity) {
        if (entity == null) {
            return null;
        }
        AuditoriaDTO dto = new AuditoriaDTO();
        dto.setEmpresaId(entity.getEmpresa() != null ? entity.getEmpresa().getId() : null);
        dto.setIdLocalEmpresa(entity.getIdLocalEmpresa());
        dto.setTipoOperacao(entity.getTipoOperacao());
        dto.setTabelaAfetada(entity.getTabelaAfetada());
        dto.setDataHora(entity.getDataHora());

        if (entity.getUsuario() != null && entity.getUsuario().getIdLocalEmpresa() != null) {
            dto.setUsuarioId(entity.getUsuario().getIdLocalEmpresa().longValue());
        }

        return dto;
    }
}