package ProjetoJava.DonodoNegocio.security;

import ProjetoJava.DonodoNegocio.model.Empresa;
import ProjetoJava.DonodoNegocio.model.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final Empresa empresa;
    private final Usuario usuario;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Empresa empresa) {
        this.empresa = empresa;
        this.usuario = null;
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    public CustomUserDetails(Usuario usuario) {
        this.usuario = usuario;
        this.empresa = usuario.getEmpresa(); // Usuário pertence a uma empresa
        
        String role = "ROLE_OPERADOR"; // Default
        if (usuario.getTipoUsuario() != null) {
            String cargo = usuario.getTipoUsuario().getCargo().toUpperCase();
            if (cargo.contains("CONSULTOR") || cargo.contains("AUDITOR")) {
                role = "ROLE_CONSULTOR";
            }
        }
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    public Long getId() {
        if (usuario != null) {
            return usuario.getId();
        }
        return empresa.getId();
    }

    public boolean isEmpresa() {
        return usuario == null;
    }

    public Empresa getEmpresaEntity() {
        return empresa;
    }

    public Usuario getUsuarioEntity() {
        return usuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        if (usuario != null) {
            return usuario.getSenhaHash();
        }
        return empresa.getSenhaHashAdmin();
    }

    @Override
    public String getUsername() {
        if (usuario != null) {
            return usuario.getLogin();
        }
        return empresa.getLoginMaster();
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
        if (usuario != null) {
            return usuario.isAtivo();
        }
        return true; // Empresa sempre ativa por enquanto
    }
}