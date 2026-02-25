package ProjetoJava.DonodoNegocio.config;

import ProjetoJava.DonodoNegocio.security.CustomUserDetails;
import ProjetoJava.DonodoNegocio.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final AuditService auditService;

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> handleNullPointerException(NullPointerException ex) {
        return processError(ex, HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro interno no servidor. Por favor, tente novamente mais tarde.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return processError(ex, HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro inesperado. Por favor, contate o suporte.");
    }

    private ResponseEntity<String> processError(Exception ex, HttpStatus status, String userMessage) {
        logDetailedError(ex, status);
        return new ResponseEntity<>(userMessage, status);
    }

    private void logDetailedError(Exception ex, HttpStatus httpStatus) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String responsibleInfo = "Não autenticado";
        CustomUserDetails userDetails = null;

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails details) {
            userDetails = details;
            responsibleInfo = String.format("Usuário/Empresa: %s (ID: %d, Admin: %b)", 
                userDetails.getUsername(), userDetails.getId(), userDetails.isEmpresa());
        }

        String errorLocation = extractErrorLocation(ex);

        logger.error(
                """
                Erro capturado pelo GlobalExceptionHandler: {} - Status HTTP: {}
                Responsável: {}
                Local do Erro: {}
                Mensagem da Exceção: {}
                """,
                ex.getClass().getSimpleName(), httpStatus.value(),
                responsibleInfo,
                errorLocation,
                ex.getMessage(),
                ex);

        if (userDetails != null) {
            auditService.logError(userDetails, errorLocation);
        }
    }

    private String extractErrorLocation(Exception ex) {
        StackTraceElement[] stackTrace = ex.getStackTrace();
        if (stackTrace.length > 0) {
            StackTraceElement element = stackTrace[0];
            return String.format("Classe: %s, Método: %s, Linha: %d", 
                element.getClassName(), element.getMethodName(), element.getLineNumber());
        }
        return "Desconhecido";
    }
}