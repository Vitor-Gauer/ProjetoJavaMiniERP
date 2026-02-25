package ProjetoJava.DonodoNegocio.controller;

import ProjetoJava.DonodoNegocio.config.AppConstants;
import ProjetoJava.DonodoNegocio.util.ValidationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class NavigationController {

    @GetMapping("/")
    public String index() {
        return AppConstants.VIEW_INDEX;
    }

    @GetMapping("/{pagina}")
    public String pagina(@PathVariable String pagina) {
        return resolverView(pagina, "");
    }

    @GetMapping("/tabela/{nomeTabela}")
    public String tabela(@PathVariable String nomeTabela) {
        return resolverView(nomeTabela, "tabela/");
    }

    @GetMapping("/relatorio/{nomeRelatorio}")
    public String relatorio(@PathVariable String nomeRelatorio) {
        return resolverView(nomeRelatorio, "relatorio/");
    }

    private String resolverView(String nome, String prefixo) {
        if (!ValidationUtils.isValidPath(nome)) {
            return AppConstants.VIEW_ERROR_404;
        }
        return prefixo + nome;
    }
}