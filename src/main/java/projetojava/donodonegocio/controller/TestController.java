package projetojava.donodonegocio.controller;

import projetojava.donodonegocio.model.Empresa;
import projetojava.donodonegocio.model.Usuario;
import projetojava.donodonegocio.model.TipoUsuario;
import projetojava.donodonegocio.repository.EmpresaRepository;
import projetojava.donodonegocio.repository.UsuarioRepository;
import projetojava.donodonegocio.repository.TipoUsuarioRepository;
import projetojava.donodonegocio.security.CustomUserDetails;
import projetojava.donodonegocio.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;
    private final TipoUsuarioRepository tipoUsuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/create-test-company")
    public ResponseEntity<Map<String, Object>> createTestCompany() {
        try {
            // Check if test company already exists
            Optional<Empresa> existing = empresaRepository.findByLoginMaster("testempresa");
            if (existing.isPresent()) {
                return ResponseEntity.ok(Map.of(
                    "message", "Test company already exists",
                    "login", "testempresa",
                    "password", "test123"
                ));
            }

            // Create test company
            Empresa empresa = new Empresa();
            empresa.setNome("Test Empresa");
            empresa.setLoginMaster("testempresa");
            empresa.setLoginPublico("testpublic");
            empresa.setSenhaHashAdmin(passwordEncoder.encode("test123"));
            empresa.setSenhaHashPublica(passwordEncoder.encode("test123"));
            
            empresa = empresaRepository.save(empresa);

            // Create test user
            Optional<TipoUsuario> operadorTipo = tipoUsuarioRepository.findAll().stream()
                    .filter(t -> t.getCargo().equalsIgnoreCase("OPERADOR"))
                    .findFirst();
            
            if (operadorTipo.isPresent()) {
                Usuario usuario = new Usuario();
                usuario.setLogin("testuser");
                usuario.setSenhaHash(passwordEncoder.encode("test123"));
                usuario.setEmpresa(empresa);
                usuario.setTipoUsuario(operadorTipo.get());
                usuario.setIdLocalEmpresa(1);
                usuarioRepository.save(usuario);
            }

            return ResponseEntity.ok(Map.of(
                "message", "Test company created successfully",
                "companyLogin", "testempresa",
                "companyPassword", "test123",
                "userLogin", "testuser",
                "userPassword", "test123"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to create test company: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/test-auth")
    public ResponseEntity<Map<String, Object>> testAuth(@RequestParam String login, @RequestParam String senha) {
        try {
            boolean exists = empresaRepository.findByLoginMaster(login).isPresent();
            boolean authSuccess = authService.authenticateEmpresa(login, senha, null).isPresent();
            
            return ResponseEntity.ok(Map.of(
                "login", login,
                "exists", exists,
                "authSuccess", authSuccess
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/auth-status")
    public ResponseEntity<Map<String, Object>> getAuthStatus() {
        
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.ok(Map.of(
                "authenticated", false,
                "message", "No authentication found"
            ));
        }

        Object principal = auth.getPrincipal();
        Map<String, Object> response = new HashMap<>();
        response.put("authenticated", true);
        response.put("username", auth.getName());
        response.put("authorities", auth.getAuthorities());
        
        if (principal instanceof CustomUserDetails) {
            CustomUserDetails details = (CustomUserDetails) principal;
            response.put("empresaId", details.getEmpresaId());
            response.put("usuarioId", details.getUsuarioId());
            response.put("isEmpresa", details.isEmpresa());
        } else if (principal.getClass().getSimpleName().contains("JwtUserDetails")) {
            // Handle JWT user details
            try {
                Long empresaId = (Long) principal.getClass().getMethod("getEmpresaId").invoke(principal);
                Long usuarioId = (Long) principal.getClass().getMethod("getUsuarioId").invoke(principal);
                Boolean isEmpresa = (Boolean) principal.getClass().getMethod("isEmpresa").invoke(principal);
                
                response.put("empresaId", empresaId);
                response.put("usuarioId", usuarioId);
                response.put("isEmpresa", isEmpresa);
                response.put("authType", "JWT");
            } catch (Exception e) {
                response.put("authType", "JWT (details extraction failed)");
            }
        }

        return ResponseEntity.ok(response);
    }
}
