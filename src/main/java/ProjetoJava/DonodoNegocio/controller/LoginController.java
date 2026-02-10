package ProjetoJava.DonodoNegocio.controller;

import ProjetoJava.DonodoNegocio.model.Empresa;
import ProjetoJava.DonodoNegocio.model.Usuario;
import ProjetoJava.DonodoNegocio.repository.EmpresaRepository;
import ProjetoJava.DonodoNegocio.repository.UsuarioRepository;
import ProjetoJava.DonodoNegocio.security.CustomUserDetails;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    private static final String REDIRECT_DASHBOARD = "redirect:/dashboard";
    private static final String REDIRECT_LOGIN = "redirect:/login";
    private static final String REDIRECT_LOGIN_USUARIO = "redirect:/login/usuario";
    private static final String ATTR_EMPRESA_ID = "empresaId";
    private static final String ATTR_EMPRESA_NOME = "empresaNome";
    private static final String VIEW_LOGIN_EMPRESA = "login-empresa";
    private static final String VIEW_LOGIN_USUARIO = "login-usuario";

    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    @GetMapping
    public String loginEmpresaPage() {
        return VIEW_LOGIN_EMPRESA;
    }

    @PostMapping("/empresa")
    public String loginEmpresa(@RequestParam String login, @RequestParam String senha, HttpSession session, Model model) {
        Optional<Empresa> empresaOpt = empresaRepository.findByLoginPublico(login);
        
        boolean isMaster = false;
        if (empresaOpt.isEmpty()) {
            empresaOpt = empresaRepository.findByLoginMaster(login);
            isMaster = true;
        }

        if (empresaOpt.isPresent()) {
            Empresa empresa = empresaOpt.get();
            
            if (isMaster) {
                if (passwordEncoder.matches(senha, empresa.getSenhaHashAdmin())) {
                    autenticarUsuario(new CustomUserDetails(empresa));
                    return REDIRECT_DASHBOARD;
                }
            } else {
                if (passwordEncoder.matches(senha, empresa.getSenhaHashPublica())) {
                    session.setAttribute(ATTR_EMPRESA_ID, empresa.getId());
                    session.setAttribute(ATTR_EMPRESA_NOME, empresa.getNome());
                    return REDIRECT_LOGIN_USUARIO;
                }
            }
        }

        model.addAttribute("error", "Empresa ou senha inválidos");
        dispararEventoFalha(login);
        return VIEW_LOGIN_EMPRESA;
    }

    @GetMapping("/usuario")
    public String loginUsuarioPage(HttpSession session, Model model) {
        Long empresaId = (Long) session.getAttribute(ATTR_EMPRESA_ID);
        if (empresaId == null) {
            return REDIRECT_LOGIN;
        }
        model.addAttribute(ATTR_EMPRESA_NOME, session.getAttribute(ATTR_EMPRESA_NOME));
        return VIEW_LOGIN_USUARIO;
    }

    @PostMapping("/usuario")
    public String loginUsuario(@RequestParam String login, @RequestParam String senha, HttpSession session, Model model) {
        Long empresaId = (Long) session.getAttribute(ATTR_EMPRESA_ID);
        if (empresaId == null) {
            return REDIRECT_LOGIN;
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByLoginAndEmpresaId(login, empresaId);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (passwordEncoder.matches(senha, usuario.getSenhaHash())) {
                autenticarUsuario(new CustomUserDetails(usuario));
                session.removeAttribute(ATTR_EMPRESA_ID);
                return REDIRECT_DASHBOARD;
            }
        } else {
            Optional<Empresa> empresaOpt = empresaRepository.findById(empresaId);
            if (empresaOpt.isPresent()) {
                Empresa empresa = empresaOpt.get();
                if (empresa.getLoginMaster().equals(login) && passwordEncoder.matches(senha, empresa.getSenhaHashAdmin())) {
                    autenticarUsuario(new CustomUserDetails(empresa));
                    session.removeAttribute(ATTR_EMPRESA_ID);
                    return REDIRECT_DASHBOARD;
                }
            }
        }

        model.addAttribute("error", "Usuário ou senha inválidos");
        Optional<Empresa> emp = empresaRepository.findById(empresaId);
        String prefix = emp.map(Empresa::getLoginPublico).orElse("UNKNOWN");
        dispararEventoFalha(prefix + "/" + login);
        
        return VIEW_LOGIN_USUARIO;
    }

    private void autenticarUsuario(CustomUserDetails userDetails) {
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        eventPublisher.publishEvent(new AuthenticationSuccessEvent(auth));
    }

    private void dispararEventoFalha(String username) {
        Authentication auth = new UsernamePasswordAuthenticationToken(username, "hidden");
        eventPublisher.publishEvent(new AuthenticationFailureBadCredentialsEvent(auth, new org.springframework.security.authentication.BadCredentialsException("Falha manual")));
    }
}