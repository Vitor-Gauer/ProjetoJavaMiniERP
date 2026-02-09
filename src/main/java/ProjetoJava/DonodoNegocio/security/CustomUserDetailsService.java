package ProjetoJava.DonodoNegocio.security;

import ProjetoJava.DonodoNegocio.model.Empresa;
import ProjetoJava.DonodoNegocio.model.Usuario;
import ProjetoJava.DonodoNegocio.repository.EmpresaRepository;
import ProjetoJava.DonodoNegocio.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Tenta encontrar como Empresa (Admin)
        Optional<Empresa> empresaOpt = empresaRepository.findByLoginMaster(username);
        if (empresaOpt.isPresent()) {
            return new CustomUserDetails(empresaOpt.get());
        }

        // 2. Tenta encontrar como Usuário (Operador/Consultor)
        Optional<Usuario> usuarioOpt = usuarioRepository.findByLogin(username);
        if (usuarioOpt.isPresent()) {
            return new CustomUserDetails(usuarioOpt.get());
        }

        throw new UsernameNotFoundException("Usuário ou Empresa não encontrados com o login: " + username);
    }
}