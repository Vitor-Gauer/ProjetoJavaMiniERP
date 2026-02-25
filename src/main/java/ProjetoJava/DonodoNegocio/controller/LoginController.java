package ProjetoJava.DonodoNegocio.controller;

import ProjetoJava.DonodoNegocio.config.AppConstants;
import ProjetoJava.DonodoNegocio.security.AuthResponse;
import ProjetoJava.DonodoNegocio.security.CustomUserDetails;
import ProjetoJava.DonodoNegocio.service.AuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

    private final AuthService authService;

    @GetMapping
    public String loginEmpresaPage() {
        return AppConstants.VIEW_LOGIN_EMPRESA;
    }

    @PostMapping("/empresa")
    public String loginEmpresa(@RequestParam String login, @RequestParam String senha, HttpSession session, Model model) {
        Optional<AuthResponse> authResponseOpt = authService.authenticateEmpresa(login, senha);

        if (authResponseOpt.isPresent()) {
            AuthResponse authResponse = authResponseOpt.get();
            
            if (authResponse.isMasterLogin()) {
                authService.definirAutenticacaoNoContexto(authResponse.getUserDetails());
                return AppConstants.REDIRECT_DASHBOARD;
            } else {
                session.setAttribute(AppConstants.ATTR_EMPRESA_ID, authResponse.getUserDetails().getEmpresaId());
                session.setAttribute(AppConstants.ATTR_EMPRESA_NOME, authResponse.getEmpresaNome());
                return AppConstants.REDIRECT_LOGIN_USUARIO;
            }
        }

        model.addAttribute("error", "Empresa ou senha inválidos");
        return AppConstants.VIEW_LOGIN_EMPRESA;
    }

    @GetMapping("/usuario")
    public String loginUsuarioPage(HttpSession session, Model model) {
        Long empresaId = (Long) session.getAttribute(AppConstants.ATTR_EMPRESA_ID);
        if (empresaId == null) {
            return AppConstants.REDIRECT_LOGIN;
        }
        model.addAttribute(AppConstants.ATTR_EMPRESA_NOME, session.getAttribute(AppConstants.ATTR_EMPRESA_NOME));
        return AppConstants.VIEW_LOGIN_USUARIO;
    }

    @PostMapping("/usuario")
    public String loginUsuario(@RequestParam String login, @RequestParam String senha, HttpSession session, Model model) {
        Long empresaId = (Long) session.getAttribute(AppConstants.ATTR_EMPRESA_ID);
        if (empresaId == null) {
            return AppConstants.REDIRECT_LOGIN;
        }

        Optional<CustomUserDetails> userDetailsOpt = authService.authenticateUsuario(login, senha, empresaId);

        if (userDetailsOpt.isPresent()) {
            authService.definirAutenticacaoNoContexto(userDetailsOpt.get());
            session.removeAttribute(AppConstants.ATTR_EMPRESA_ID);
            session.removeAttribute(AppConstants.ATTR_EMPRESA_NOME);
            return AppConstants.REDIRECT_DASHBOARD;
        }

        model.addAttribute("error", "Usuário ou senha inválidos");
        model.addAttribute(AppConstants.ATTR_EMPRESA_NOME, session.getAttribute(AppConstants.ATTR_EMPRESA_NOME));
        return AppConstants.VIEW_LOGIN_USUARIO;
    }
}