package ProjetoJava.DonodoNegocio.mapper;

import ProjetoJava.DonodoNegocio.dto.UsuarioDTO;
import ProjetoJava.DonodoNegocio.model.Empresa;
import ProjetoJava.DonodoNegocio.model.Usuario;
import ProjetoJava.DonodoNegocio.repository.TipoUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    @Autowired
    private TipoUsuarioRepository tipoUsuarioRepository;

    public Usuario toEntity(UsuarioDTO dto) {
        if (dto == null) {
            return null;
        }
        Usuario entity = new Usuario();
        entity.setIdLocalEmpresa(dto.getIdLocalEmpresa() != null ? dto.getIdLocalEmpresa().intValue() : null);
        entity.setLogin(dto.getLogin());
        entity.setAtivo(dto.isAtivo());
        
        // Mapeando senha (crua) para o campo de hash, conforme solicitado.
        entity.setSenhaHash(dto.getSenha());

        if (dto.getEmpresaId() != null) {
            Empresa empresa = new Empresa();
            empresa.setId(dto.getEmpresaId());
            entity.setEmpresa(empresa);

            if (dto.getTipoUsuarioId() != null) {
                tipoUsuarioRepository.findByEmpresaIdAndIdLocalEmpresa(dto.getEmpresaId(), dto.getTipoUsuarioId().intValue())
                        .ifPresent(entity::setTipoUsuario);
            }
        }

        return entity;
    }

    public UsuarioDTO toDTO(Usuario entity) {
        if (entity == null) {
            return null;
        }
        UsuarioDTO dto = new UsuarioDTO();
        dto.setEmpresaId(entity.getEmpresa() != null ? entity.getEmpresa().getId() : null);
        dto.setIdLocalEmpresa(entity.getIdLocalEmpresa() != null ? entity.getIdLocalEmpresa().longValue() : null);
        dto.setLogin(entity.getLogin());
        dto.setAtivo(entity.isAtivo());
        // Senha não retornada

        if (entity.getTipoUsuario() != null && entity.getTipoUsuario().getIdLocalEmpresa() != null) {
            dto.setTipoUsuarioId(entity.getTipoUsuario().getIdLocalEmpresa().longValue());
        }

        return dto;
    }

    public void updateEntityFromDTO(UsuarioDTO dto, Usuario entity) {
        if (dto == null || entity == null) {
            return;
        }
        if (dto.getIdLocalEmpresa() != null) {
            entity.setIdLocalEmpresa(dto.getIdLocalEmpresa().intValue());
        }
        entity.setLogin(dto.getLogin());
        entity.setAtivo(dto.isAtivo());
        
        if (dto.getSenha() != null) {
            entity.setSenhaHash(dto.getSenha());
        }
        
        if (dto.getEmpresaId() != null) {
            if (entity.getEmpresa() == null || !entity.getEmpresa().getId().equals(dto.getEmpresaId())) {
                Empresa empresa = new Empresa();
                empresa.setId(dto.getEmpresaId());
                entity.setEmpresa(empresa);
            }

            if (dto.getTipoUsuarioId() != null) {
                tipoUsuarioRepository.findByEmpresaIdAndIdLocalEmpresa(dto.getEmpresaId(), dto.getTipoUsuarioId().intValue())
                        .ifPresent(entity::setTipoUsuario);
            }
        }
    }
}