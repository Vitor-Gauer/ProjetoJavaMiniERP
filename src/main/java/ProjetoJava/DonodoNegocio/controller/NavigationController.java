package ProjetoJava.DonodoNegocio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class NavigationController {

    @GetMapping("/") // Spring já faz Welcome Page -> templates/index.html, isso é apenas para explicitar
    public String index() {
        return "index";
    }

    @GetMapping("/{pagina}") // tudo que tiver em templates
    public String pagina(@PathVariable String pagina) {
        return pagina; // (dashboard, etc.)
    }

    @GetMapping("/tabela/{nomeTabela}") // tudo que tiver em templates/tabela
    public String tabela(@PathVariable String nomeTabela) {
        return "tabela/" + nomeTabela; // (cliente, produto, etc.)
    }

    @GetMapping("/relatorio/{nomeRelatorio}") // tudo que tiver em templates/relatorio
    public String relatorio(@PathVariable String nomeRelatorio) {
        return "relatorio/" + nomeRelatorio; // (compras, vendas, etc.)
    }
}
