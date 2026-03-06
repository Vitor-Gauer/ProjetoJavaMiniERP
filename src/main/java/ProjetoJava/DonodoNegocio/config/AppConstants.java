package ProjetoJava.DonodoNegocio.config;

public final class AppConstants {

    private AppConstants() {

    }

    public static final String REGEX_SAFE_PATH = "^[a-zA-Z0-9_-]+$";
    public static final String VIEW_ERROR_404 = "error/404";
    public static final String VIEW_INDEX = "index";
    public static final String VIEW_LOGIN_EMPRESA = "login-empresa";
    public static final String VIEW_LOGIN_USUARIO = "login-usuario";
    
    public static final String REDIRECT_DASHBOARD = "redirect:/dashboard";
    public static final String REDIRECT_LOGIN = "redirect:/login";
    public static final String REDIRECT_LOGIN_USUARIO = "redirect:/login/usuario";
    
    public static final String ATTR_EMPRESA_ID = "empresaId";
    public static final String ATTR_EMPRESA_NOME = "empresaNome";
}