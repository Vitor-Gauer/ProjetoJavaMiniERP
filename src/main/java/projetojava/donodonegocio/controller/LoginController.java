package projetojava.donodonegocio.controller;

import projetojava.donodonegocio.config.AppConstants;
import projetojava.donodonegocio.dto.LoginDTO;
import projetojava.donodonegocio.security.AuthResponse;
import projetojava.donodonegocio.security.CustomUserDetails;
import projetojava.donodonegocio.service.AuthService;
import projetojava.donodonegocio.service.LoginAttemptService;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {
    private static final String ERROR = "error";
    private final AuthService authService;
    private final LoginAttemptService loginAttemptService;

    @GetMapping
    public String loginEmpresaPage(Model model) {
        model.addAttribute("loginDto", new LoginDTO());
        return AppConstants.VIEW_LOGIN_EMPRESA;
    }

    @PostMapping("/empresa")
    public String loginEmpresa(@Valid @ModelAttribute("loginDto") LoginDTO loginDto,
                               BindingResult bindingResult,
                               HttpSession session,
                               HttpServletResponse response,
                               Model model) {

        if (bindingResult.hasErrors()) {
            return AppConstants.VIEW_LOGIN_EMPRESA;
        }

        String rateLimitKey = "empresa:" + loginDto.getLogin();
        if (loginAttemptService.isBlocked(rateLimitKey)) {
            model.addAttribute(ERROR, "Muitas tentativas de login. Tente novamente em 10 segundos.");
            return AppConstants.VIEW_LOGIN_EMPRESA;
        }

        Optional<AuthResponse> authResponseOpt = authService.authenticateEmpresa(
                loginDto.getLogin(),
                loginDto.getSenha(),
                response
        );

        if (authResponseOpt.isPresent()) {
            AuthResponse authResponse = authResponseOpt.get();

            if (authResponse.isMasterLogin()) {
                authService.definirAutenticacaoNoContexto(authResponse.userDetails());
                return AppConstants.REDIRECT_DASHBOARD;
            } else {
                session.setAttribute(AppConstants.ATTR_EMPRESA_ID, authResponse.userDetails().getEmpresaId());
                session.setAttribute(AppConstants.ATTR_EMPRESA_NOME, authResponse.empresaNome());
                return AppConstants.REDIRECT_LOGIN_USUARIO;
            }
        }

        model.addAttribute(ERROR, "Empresa ou senha inválidos");
        return AppConstants.VIEW_LOGIN_EMPRESA;
    }

    @GetMapping("/usuario")
    public String loginUsuarioPage(HttpSession session, Model model) {
        Long empresaId = (Long) session.getAttribute(AppConstants.ATTR_EMPRESA_ID);
        if (empresaId == null) {
            return AppConstants.REDIRECT_LOGIN;
        }

        model.addAttribute("loginDto", new LoginDTO());
        model.addAttribute(AppConstants.ATTR_EMPRESA_NOME, session.getAttribute(AppConstants.ATTR_EMPRESA_NOME));
        return AppConstants.VIEW_LOGIN_USUARIO;
    }

    @PostMapping("/usuario")
    public String loginUsuario(@Valid @ModelAttribute("loginDto") LoginDTO loginDto,
                               BindingResult bindingResult,
                               HttpSession session,
                               HttpServletResponse response,
                               Model model) {

        Long empresaId = (Long) session.getAttribute(AppConstants.ATTR_EMPRESA_ID);
        if (empresaId == null) {
            return AppConstants.REDIRECT_LOGIN;
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute(AppConstants.ATTR_EMPRESA_NOME, session.getAttribute(AppConstants.ATTR_EMPRESA_NOME));
            return AppConstants.VIEW_LOGIN_USUARIO;
        }

        String rateLimitKey = "usuario:" + empresaId + ":" + loginDto.getLogin();
        if (loginAttemptService.isBlocked(rateLimitKey)) {
            model.addAttribute(ERROR, "Muitas tentativas de login. Tente novamente em 10 segundos.");
            model.addAttribute(AppConstants.ATTR_EMPRESA_NOME, session.getAttribute(AppConstants.ATTR_EMPRESA_NOME));
            return AppConstants.VIEW_LOGIN_USUARIO;
        }

        Optional<CustomUserDetails> userDetailsOpt = authService.authenticateUsuario(
                loginDto.getLogin(),
                loginDto.getSenha(),
                empresaId,
                response
        );

        if (userDetailsOpt.isPresent()) {
            authService.definirAutenticacaoNoContexto(userDetailsOpt.get());

            session.removeAttribute(AppConstants.ATTR_EMPRESA_ID);
            session.removeAttribute(AppConstants.ATTR_EMPRESA_NOME);
            return AppConstants.REDIRECT_DASHBOARD;
        }

        model.addAttribute(ERROR, "Usuário ou senha inválidos");
        model.addAttribute(AppConstants.ATTR_EMPRESA_NOME, session.getAttribute(AppConstants.ATTR_EMPRESA_NOME));
        return AppConstants.VIEW_LOGIN_USUARIO;
    }
}