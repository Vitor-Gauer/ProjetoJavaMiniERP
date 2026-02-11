package ProjetoJava.DonodoNegocio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class NavigationController {
    private static final String E404 = "error/404";
    private static final String REGEX_SAFE_PATH = "^[a-zA-Z0-9_-]+$";
    @GetMapping("/") 
    public String index() {
        return "index";
    }

    @GetMapping("/{pagina}") 
    public String pagina(@PathVariable String pagina) {
        if (!pagina.matches(REGEX_SAFE_PATH)) {
            return E404;
        }
        return pagina; 
    }

    @GetMapping("/tabela/{nomeTabela}") 
    public String tabela(@PathVariable String nomeTabela) {
        if (!nomeTabela.matches(REGEX_SAFE_PATH)) {
            return E404;
        }
        return "tabela/" + nomeTabela; 
    }

    @GetMapping("/relatorio/{nomeRelatorio}") 
    public String relatorio(@PathVariable String nomeRelatorio) {
        if (!nomeRelatorio.matches(REGEX_SAFE_PATH)) {
            return E404;
        }
        return "relatorio/" + nomeRelatorio; 
    }
}