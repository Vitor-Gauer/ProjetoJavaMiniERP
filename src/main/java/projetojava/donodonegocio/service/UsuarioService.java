package projetojava.donodonegocio.service;

import projetojava.donodonegocio.dto.UsuarioDTO;
import projetojava.donodonegocio.mapper.UsuarioMapper;
import projetojava.donodonegocio.model.Usuario;
import projetojava.donodonegocio.repository.UsuarioRepository;
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
        if (dto.getIdLocalEmpresa() == null) {
            return criarNovoUsuario(dto);
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmpresaIdAndIdLocalEmpresa(dto.getEmpresaId(), dto.getIdLocalEmpresa().intValue());
        
        return usuarioOpt
                .map(usuario -> atualizarUsuarioExistente(dto, usuario))
                .orElseGet(() -> criarNovoUsuario(dto));
    }

    private UsuarioDTO criarNovoUsuario(UsuarioDTO dto) {
        Usuario novoUsuario = usuarioMapper.toEntity(dto);
        
        Integer maxId = usuarioRepository.findMaxIdLocalByEmpresaId(dto.getEmpresaId());
        int proximoId = (maxId == null) ? 1 : maxId + 1;
        novoUsuario.setIdLocalEmpresa(proximoId);
        
        processarSenha(dto, novoUsuario);
        
        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);
        return usuarioMapper.toDTO(usuarioSalvo);
    }

    private UsuarioDTO atualizarUsuarioExistente(UsuarioDTO dto, Usuario usuario) {
        usuarioMapper.updateEntityFromDTO(dto, usuario);
        processarSenha(dto, usuario);
        Usuario usuarioAtualizado = usuarioRepository.save(usuario);
        return usuarioMapper.toDTO(usuarioAtualizado);
    }

    private void processarSenha(UsuarioDTO dto, Usuario entity) {
        if (dto.getSenha() != null && !dto.getSenha().isEmpty()) {
            entity.setSenhaHash(passwordEncoder.encode(dto.getSenha()));
        }
    }
}