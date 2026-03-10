package projetojava.donodonegocio.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class JwtUserDetails implements UserDetails {

    private final Long empresaId;
    private final Long usuarioId;
    private final Integer idLocalEmpresa;
    private final String username;
    private final String password;
    private final boolean isEmpresa;
    private final boolean isAtivo;
    private final Collection<? extends GrantedAuthority> authorities;

    public JwtUserDetails(String username, Long empresaId, Long usuarioId, String role) {
        this.empresaId = empresaId;
        this.usuarioId = usuarioId;
        this.idLocalEmpresa = null;
        this.username = username;
        this.password = "";
        this.isEmpresa = usuarioId == null;
        this.isAtivo = true;
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    public Long getEmpresaId() {
        return empresaId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public Integer getIdLocalEmpresa() {
        return idLocalEmpresa;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isEmpresa() {
        return isEmpresa;
    }

    public boolean isAtivo() {
        return isAtivo;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
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
