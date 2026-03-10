package projetojava.donodonegocio.service;

import projetojava.donodonegocio.security.CustomUserDetails;
import projetojava.donodonegocio.repository.TransacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CobrancaAvisoService {

    private static final String ATTR_AVISO_COBRANCA_VENCIDA = "avisoCobrancaVencida";

    private final TransacaoRepository transacaoRepository;

    public void marcarAvisoNaSessaoSeNecessario(CustomUserDetails userDetails, HttpSession session) {
        if (session == null || userDetails == null) {
            return;
        }

        if (Boolean.TRUE.equals(session.getAttribute(ATTR_AVISO_COBRANCA_VENCIDA))) {
            return;
        }

        if (!temRoleApropriada(userDetails)) {
            return;
        }

        boolean existe = transacaoRepository.existsCobrancaVencidaNaoResolvida(userDetails.getEmpresaId(), LocalDateTime.now());
        if (existe) {
            session.setAttribute(ATTR_AVISO_COBRANCA_VENCIDA, true);
        }
    }

    private boolean temRoleApropriada(CustomUserDetails userDetails) {
        return userDetails.getAuthorities().stream().anyMatch(a -> {
            String role = a.getAuthority();
            return "ROLE_ADMIN".equals(role) || "ROLE_OPERADOR".equals(role) || "ROLE_CONSULTOR".equals(role);
        });
    }
}
