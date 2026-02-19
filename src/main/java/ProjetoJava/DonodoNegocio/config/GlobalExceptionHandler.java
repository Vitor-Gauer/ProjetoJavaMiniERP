package ProjetoJava.DonodoNegocio.config;

import ProjetoJava.DonodoNegocio.model.Auditoria;
import ProjetoJava.DonodoNegocio.model.Empresa;
import ProjetoJava.DonodoNegocio.repository.AuditoriaRepository;
import ProjetoJava.DonodoNegocio.repository.EmpresaRepository;
import ProjetoJava.DonodoNegocio.repository.UsuarioRepository;
import ProjetoJava.DonodoNegocio.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final AuditoriaRepository auditoriaRepository;
    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> handleNullPointerException(NullPointerException ex) {
        String errorMessage = "Ocorreu um erro interno no servidor. Por favor, tente novamente mais tarde.";
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        // Log detalhado no backend
        logDetailedError(ex, status);

        return new ResponseEntity<>(errorMessage, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        String errorMessage = "Ocorreu um erro inesperado. Por favor, contate o suporte.";
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        // Log detalhado no backend
        logDetailedError(ex, status);

        return new ResponseEntity<>(errorMessage, status);
    }

    private void logDetailedError(Exception ex, HttpStatus httpStatus) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String responsibleInfo = "Não autenticado";
        Long empresaId = null;
        Long loginId = null;
        boolean ehAdmin = false;
        Integer idLocalEmpresa = 0; // Default para eventos de sistema

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            empresaId = userDetails.getEmpresaId();
            loginId = userDetails.getId();
            ehAdmin = userDetails.isEmpresa();
            idLocalEmpresa = userDetails.getIdLocalEmpresa() != null ? userDetails.getIdLocalEmpresa() : 0;
            responsibleInfo = String.format("Usuário/Empresa: %s (ID: %d, Admin: %b)", userDetails.getUsername(), userDetails.getId(), userDetails.isEmpresa());
        }

        // Tenta encontrar a origem do NPE no código
        StackTraceElement[] stackTrace = ex.getStackTrace();
        String errorLocation = "Desconhecido";
        if (stackTrace.length > 0) {
            StackTraceElement element = stackTrace[0]; // Pega o primeiro elemento da pilha (onde a exceção ocorreu)
            errorLocation = String.format("Classe: %s, Método: %s, Linha: %d", element.getClassName(), element.getMethodName(), element.getLineNumber());
        }

        logger.error(
                """
                Erro capturado pelo GlobalExceptionHandler: {} - Status HTTP: {}
                Responsável: {}
                Local do Erro: {}
                Mensagem da Exceção: {}
                Stack Trace:\s
                """,
                ex.getClass().getSimpleName(), httpStatus.value(),
                responsibleInfo,
                errorLocation,
                ex.getMessage(),
                ex); // Passa a exceção inteira para imprimir o stack trace completo

        // Salvar auditoria do erro
        if (empresaId != null) {
            try {
                Auditoria auditoria = new Auditoria();
                Empresa empresaStub = new Empresa();
                empresaStub.setId(empresaId);
                auditoria.setEmpresa(empresaStub);
                auditoria.setLoginId(loginId);
                auditoria.setEhAdmin(ehAdmin);
                auditoria.setIdLocalEmpresa(idLocalEmpresa);
                auditoria.setTipoOperacao("ERRO_SISTEMA");
                auditoria.setTabelaAfetada(errorLocation.length() > 50 ? errorLocation.substring(0, 50) : errorLocation); // Limita para caber na coluna
                auditoria.setDataHora(LocalDateTime.now());
                auditoriaRepository.save(auditoria);
            } catch (Exception auditEx) {
                logger.error("Erro ao salvar auditoria do erro: {}", auditEx.getMessage());
            }
        }
    }
}