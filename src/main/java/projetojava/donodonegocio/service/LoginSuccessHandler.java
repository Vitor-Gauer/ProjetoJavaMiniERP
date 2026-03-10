package projetojava.donodonegocio.service;

import projetojava.donodonegocio.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.Nonnull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    
    private final CobrancaService cobrancaService;
    
    @Override
    public void onAuthenticationSuccess(@Nonnull HttpServletRequest request, 
                                      @Nonnull HttpServletResponse response, 
                                      @Nonnull Authentication authentication) throws IOException {
        
        Object principal = authentication.getPrincipal();
        
        boolean temCobrancasPendentes = false;
        Long userId = null;
        
        if (hasAppropriateRole(authentication) && principal instanceof CustomUserDetails userDetails) {
            userId = userDetails.getId();
            Long empresaId = userDetails.getEmpresaId();
            
            if (empresaId != null) {
                temCobrancasPendentes = cobrancaService.verificarCobrancasPendentes(
                    empresaId, 
                    userId
                );
            }
        }
        
        HttpSession session = request.getSession();
        session.setAttribute("temCobrancasPendentes", temCobrancasPendentes);
        
        if (temCobrancasPendentes && userId != null) {
            log.info("Notificação de cobranças pendentes gerada para usuário {}", userId);
        }
        
        response.sendRedirect("/dashboard");
    }
    
    private boolean hasAppropriateRole(@Nonnull Authentication authentication) {
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) ||
               authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_GERENCIA"));
    }
}
