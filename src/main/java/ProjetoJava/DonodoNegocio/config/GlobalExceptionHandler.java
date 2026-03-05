package ProjetoJava.DonodoNegocio.config;

import ProjetoJava.DonodoNegocio.security.CustomUserDetails;
import ProjetoJava.DonodoNegocio.service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final AuditService auditService;

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException(Exception ex, HttpServletRequest request) {
        String errorLocation = extractErrorLocation(ex);
        logDetailedError(ex, errorLocation);
        ModelAndView mav = new ModelAndView("forward:/error/500");
        request.setAttribute("message", "Ocorreu um erro interno no servidor. Por favor, tente novamente mais tarde.");
        return mav;
    }

    private void logDetailedError(Exception ex, String errorLocation) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userInfo = "Não autenticado";

        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails user) {
            userInfo = String.format("ID Usuário: %d", user.getId());
        }

        logger.error(
                """                
                        [GEH] Erro Capturado
                        Tipo: {}
                        Local: {}
                        Sessão: {}
                        Mensagem Original: {}
                        """,
                ex.getClass().getSimpleName(),
                errorLocation,
                userInfo,
                ex.getMessage(),
                ex);

        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails user) {
            auditService.logError(user, errorLocation);
        }
    }

    private String extractErrorLocation(Exception ex) {
        return Arrays.stream(ex.getStackTrace())
                .filter(ste -> ste.getClassName().startsWith("ProjetoJava"))
                .filter(ste -> !ste.getClassName().contains("GlobalExceptionHandler"))
                .findFirst()
                .map(ste -> {
                    String simpleClass = ste.getClassName().substring(ste.getClassName().lastIndexOf('.') + 1);
                    return simpleClass + "." + ste.getMethodName() + ":" + ste.getLineNumber();
                })
                .orElse("Origem Desconhecida");
    }
}