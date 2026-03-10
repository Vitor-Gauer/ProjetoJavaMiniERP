package projetojava.donodonegocio.config;

public final class AppConstants {

    private AppConstants() {

    }

    public static final String REGEX_SAFE_PATH = "^[a-zA-Z0-9_-]+$";
    public static final String VIEW_ERROR_404 = "error/404";
    public static final String VIEW_INDEX = "index";
    public static final String VIEW_LOGIN_EMPRESA = "empresalogin";
    public static final String VIEW_LOGIN_USUARIO = "funcionariologin";



    public static final String VIEW_PAINEL = "painel";
    public static final String VIEW_EMPRESA_LOGIN_LEGACY = "empresalogin";
    public static final String VIEW_FUNCIONARIO_LOGIN_LEGACY = "funcionariologin";
    public static final String VIEW_TABELAS = "tabelas";
    public static final String VIEW_MOVIMENTACOES = "movimentacoes";
    public static final String VIEW_AUDITORIA = "auditoria";
    public static final String VIEW_CADASTRO = "cadastro";
    
    public static final String REDIRECT_DASHBOARD = "redirect:/dashboard";
    public static final String REDIRECT_LOGIN = "redirect:/login";
    public static final String REDIRECT_LOGIN_USUARIO = "redirect:/login/usuario";



    public static final String REDIRECT_PAINEL = "redirect:/painel";
    public static final String REDIRECT_TABELAS = "redirect:/tabelas";
    public static final String REDIRECT_EMPRESA_LOGIN_LEGACY = "redirect:/empresalogin";
    public static final String REDIRECT_FUNCIONARIO_LOGIN_LEGACY = "redirect:/funcionariologin";
    public static final String REDIRECT_MOVIMENTACOES = "redirect:/movimentacoes";
    public static final String REDIRECT_AUDITORIA = "redirect:/auditoria";
    public static final String REDIRECT_CADASTRO = "redirect:/cadastro";
    
    public static final String ATTR_EMPRESA_ID = "empresaId";
    public static final String ATTR_EMPRESA_NOME = "empresaNome";
    
    public static final String VIEW_CLIENTE_FORM = "cliente-form";
    
    public static final String TABELA_SISTEMA = "SISTEMA";
    public static final String ERROR_MESSAGE = "error";
    public static final String STATUS_TODOS = "&status=todos";
    public static final String REDIRECT_TABELAS_WITH_TIPO = "redirect:/tabelas?tipo=";
}