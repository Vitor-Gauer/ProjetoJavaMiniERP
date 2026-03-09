package projetojava.donodonegocio.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import projetojava.donodonegocio.security.JwtUserDetails;
import projetojava.donodonegocio.service.JwtService;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Skip JWT validation for login, cadastro, and static resources
        String path = request.getRequestURI();
        if (path.equals("/") || path.startsWith("/login") || path.startsWith("/cadastro") || 
            path.startsWith("/webjars/") || path.startsWith("/css/") || path.startsWith("/js/") || 
            path.startsWith("/assets/")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = getJwtFromRequest(request);
        
        if (jwt != null) {
            try {
                final String username = jwtService.extractUsername(jwt);
                
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    final Long empresaId = jwtService.extractEmpresaId(jwt);
                    final Long usuarioId = jwtService.extractUsuarioId(jwt);
                    final String role = jwtService.extractClaim(jwt, claims -> claims.get("role", String.class));
                    
                    if (jwtService.validateToken(jwt, username)) {
                        JwtUserDetails userDetails = createJwtUserDetails(username, empresaId, usuarioId, role);
                        
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, Collections.singletonList(new SimpleGrantedAuthority(role)));
                        
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        
                        log.debug("Set authentication for user: {} with role: {}", username, role);
                    }
                }
            } catch (Exception e) {
                log.error("JWT validation failed: {}", e.getMessage());
                // Clear authentication if token is invalid
                SecurityContextHolder.clearContext();
            }
        }
        
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        // Try to get JWT from HTTP-only cookie first
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        
        // Fallback to Authorization header (for API calls)
        final String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        return null;
    }

    private JwtUserDetails createJwtUserDetails(String username, Long empresaId, Long usuarioId, String role) {
        return new JwtUserDetails(username, empresaId, usuarioId, role);
    }
}
