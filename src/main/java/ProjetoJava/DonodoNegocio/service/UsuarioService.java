package ProjetoJava.DonodoNegocio.service;

import ProjetoJava.DonodoNegocio.dto.UsuarioDTO;
import ProjetoJava.DonodoNegocio.mapper.UsuarioMapper;
import ProjetoJava.DonodoNegocio.model.Usuario;
import ProjetoJava.DonodoNegocio.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public UsuarioDTO salvar(UsuarioDTO dto) {
        Usuario entity;
        boolean isNew = false;

        if (dto.getIdLocalEmpresa() != null) {
            Optional<Usuario> existing = usuarioRepository.findByEmpresaIdAndIdLocalEmpresa(dto.getEmpresaId(), dto.getIdLocalEmpresa().intValue());
            if (existing.isPresent()) {
                entity = existing.get();
                usuarioMapper.updateEntityFromDTO(dto, entity);
            } else {
                entity = usuarioMapper.toEntity(dto);
                isNew = true;
            }
        } else {
            entity = usuarioMapper.toEntity(dto);
            isNew = true;
        }

        if (isNew) {
            Integer maxId = usuarioRepository.findMaxIdLocalByEmpresaId(dto.getEmpresaId());
            int nextId = (maxId == null) ? 1 : maxId + 1;
            entity.setIdLocalEmpresa(nextId);
        }

        // Hash da senha
        if (dto.getSenha() != null && !dto.getSenha().isEmpty()) {
            entity.setSenhaHash(passwordEncoder.encode(dto.getSenha()));
        }

        entity = usuarioRepository.save(entity);
        return usuarioMapper.toDTO(entity);
    }
}