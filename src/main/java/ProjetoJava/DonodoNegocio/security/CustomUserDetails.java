package ProjetoJava.DonodoNegocio.security;

import ProjetoJava.DonodoNegocio.model.Empresa;
import ProjetoJava.DonodoNegocio.model.Usuario;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Long empresaId;
    private final Long usuarioId;
    private final Integer idLocalEmpresa;
    private final String username;
    private final String password;
    private final boolean isEmpresa;
    private final boolean isAtivo;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Empresa empresa) {
        this.empresaId = empresa.getId();
        this.usuarioId = null;
        this.idLocalEmpresa = null;
        this.username = empresa.getLoginMaster();
        this.password = empresa.getSenhaHashAdmin();
        this.isEmpresa = true;
        this.isAtivo = true;
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    public CustomUserDetails(Usuario usuario) {
        this.empresaId = usuario.getEmpresa().getId();
        this.usuarioId = usuario.getId();
        this.idLocalEmpresa = usuario.getIdLocalEmpresa();
        this.username = usuario.getLogin();
        this.password = usuario.getSenhaHash();
        this.isEmpresa = false;
        this.isAtivo = usuario.isAtivo();

        final String role;

        if (usuario.getTipoUsuario() != null &&
           (usuario.getTipoUsuario().getCargo().toUpperCase().contains("CONSULTOR") ||
            usuario.getTipoUsuario().getCargo().toUpperCase().contains("AUDITOR")))
        {
            role = "ROLE_CONSULTOR";
        } else {
            role = "ROLE_OPERADOR"; // O 'else' é obrigatório para garantir a inicialização
        }
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    public Long getId() {
        return isEmpresa ? empresaId : usuarioId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isAtivo;
    }
}