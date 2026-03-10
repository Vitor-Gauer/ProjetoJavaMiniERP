package projetojava.donodonegocio.controller;

import projetojava.donodonegocio.config.AppConstants;
import projetojava.donodonegocio.repository.TransacaoRepository;
import projetojava.donodonegocio.repository.ClienteRepository;
import projetojava.donodonegocio.repository.ProdutoRepository;
import projetojava.donodonegocio.repository.TesouroRepository;
import projetojava.donodonegocio.repository.UsuarioRepository;
import projetojava.donodonegocio.model.Usuario;
import projetojava.donodonegocio.model.Cliente;
import projetojava.donodonegocio.model.Produto;
import projetojava.donodonegocio.model.Tesouro;
import projetojava.donodonegocio.security.CustomUserDetails;
import projetojava.donodonegocio.service.TipoTransacaoLookupService;
import projetojava.donodonegocio.util.ValidationUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.LocalDate;

record TransacaoFilterParams(String tipo, String status, String dataInicio, String dataFim, 
                           String campoData, String sort1, String dir1, String sort2, String dir2) {}

@Controller
public class NavigationController {

    private final TransacaoRepository transacaoRepository;
    private final TipoTransacaoLookupService tipoTransacaoLookupService;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final TesouroRepository tesouroRepository;
    private final UsuarioRepository usuarioRepository;

    public NavigationController(TransacaoRepository transacaoRepository,
                                TipoTransacaoLookupService tipoTransacaoLookupService,
                                ClienteRepository clienteRepository,
                                ProdutoRepository produtoRepository,
                                TesouroRepository tesouroRepository,
                                UsuarioRepository usuarioRepository) {
        this.transacaoRepository = transacaoRepository;
        this.tipoTransacaoLookupService = tipoTransacaoLookupService;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
        this.tesouroRepository = tesouroRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/painel")
    public String painel() {
        return "painel";
    }

    @GetMapping("/tabelas")
    public String tabelas(@RequestParam(value = "tipo", required = false) String tipo,
                          @RequestParam(value = "status", required = false, defaultValue = "todos") String status,
                          @RequestParam(value = "dataInicio", required = false) String dataInicio,
                          @RequestParam(value = "dataFim", required = false) String dataFim,
                          @RequestParam(value = "campoData", required = false, defaultValue = "auto") String campoData,
                          @RequestParam(value = "sort1", required = false, defaultValue = "idLocalEmpresa") String sort1,
                          @RequestParam(value = "dir1", required = false, defaultValue = "asc") String dir1,
                          @RequestParam(value = "sort2", required = false) String sort2,
                          @RequestParam(value = "dir2", required = false, defaultValue = "asc") String dir2,
                          Model model) {
        
        if (!isTransacaoTipo(tipo)) {
            return AppConstants.VIEW_TABELAS;
        }
        
        CustomUserDetails userDetails = getUserDetails();
        if (userDetails == null) {
            return AppConstants.REDIRECT_LOGIN;
        }

        List<projetojava.donodonegocio.model.Transacao> transacoes = getFilteredTransacoes(tipo, status, dataInicio, dataFim, campoData, userDetails);
        transacoes = sortTransacoes(transacoes, sort1, dir1, sort2, dir2);
        
        TransacaoFilterParams params = new TransacaoFilterParams(tipo, status, dataInicio, dataFim, 
                                                               campoData, sort1, dir1, sort2, dir2);
        populateModelWithTransacaoData(model, transacoes, params, userDetails);
        
        return AppConstants.VIEW_TABELAS;
    }

    private boolean isTransacaoTipo(String tipo) {
        return "venda".equalsIgnoreCase(tipo) || "compra".equalsIgnoreCase(tipo);
    }

    private List<projetojava.donodonegocio.model.Transacao> getFilteredTransacoes(String tipo, String status, 
                                                                                  String dataInicio, String dataFim, 
                                                                                  String campoData, CustomUserDetails userDetails) {
        String tipoNome = "venda".equalsIgnoreCase(tipo) ? "Venda" : "Compra";
        List<projetojava.donodonegocio.model.Transacao> transacoes = transacaoRepository.findByEmpresaIdAndTipoTransacaoNome(userDetails.getEmpresaId(), tipoNome);

        transacoes = filterByStatus(transacoes, status);
        transacoes = filterByDateRange(transacoes, dataInicio, dataFim, campoData);
        
        return transacoes;
    }

    private List<projetojava.donodonegocio.model.Transacao> filterByStatus(List<projetojava.donodonegocio.model.Transacao> transacoes, String status) {
        if ("ativos".equalsIgnoreCase(status)) {
            return transacoes.stream().filter(projetojava.donodonegocio.model.Transacao::isEhValida).toList();
        } else if ("inativos".equalsIgnoreCase(status)) {
            return transacoes.stream().filter(t -> !t.isEhValida()).toList();
        } else if ("quitados".equalsIgnoreCase(status)) {
            return transacoes.stream().filter(projetojava.donodonegocio.model.Transacao::isFoiResolvido).toList();
        } else if ("devendo".equalsIgnoreCase(status)) {
            LocalDateTime agora = LocalDateTime.now();
            return transacoes.stream()
                    .filter(projetojava.donodonegocio.model.Transacao::isEhValida)
                    .filter(t -> !t.isFoiResolvido())
                    .filter(t -> t.getIntervaloCobranca() != null && t.getIntervaloCobranca() > 0)
                    .filter(t -> t.getDataCriacao() != null)
                    .filter(t -> t.getDataCriacao().plusDays(t.getIntervaloCobranca()).isBefore(agora)
                            || t.getDataCriacao().plusDays(t.getIntervaloCobranca()).isEqual(agora))
                    .toList();
        }
        return transacoes;
    }

    private List<projetojava.donodonegocio.model.Transacao> filterByDateRange(List<projetojava.donodonegocio.model.Transacao> transacoes, 
                                                                            String dataInicio, String dataFim, String campoData) {
        LocalDate inicio = parseDate(dataInicio);
        LocalDate fim = parseDate(dataFim);
        
        if (inicio == null && fim == null) {
            return transacoes;
        }
        
        final LocalDate iniF = inicio;
        final LocalDate fimF = fim;
        final String campo = campoData == null ? "auto" : campoData;

        return transacoes.stream().filter(t -> {
            LocalDate d = getDateForFiltering(t, campo);
            if (d == null) return false;
            if (iniF != null && d.isBefore(iniF)) return false;
            return fimF == null || !d.isAfter(fimF);
        }).toList();
    }

    private LocalDate getDateForFiltering(projetojava.donodonegocio.model.Transacao t, String campo) {
        if ("criacao".equalsIgnoreCase(campo)) {
            return toLocalDate(t.getDataCriacao());
        } else if ("resolucao".equalsIgnoreCase(campo)) {
            return toLocalDate(t.getDataResolucao());
        } else {
            return getAutoDate(t);
        }
    }

    private LocalDate toLocalDate(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.toLocalDate() : null;
    }

    private LocalDate getAutoDate(projetojava.donodonegocio.model.Transacao t) {
        if (t.isFoiResolvido()) {
            return toLocalDate(t.getDataResolucao());
        } else {
            return toLocalDate(t.getDataCriacao());
        }
    }

    private List<projetojava.donodonegocio.model.Transacao> sortTransacoes(List<projetojava.donodonegocio.model.Transacao> transacoes, 
                                                                          String sort1, String dir1, String sort2, String dir2) {
        Comparator<projetojava.donodonegocio.model.Transacao> comparator = buildComparator(sort1, dir1, sort2, dir2);
        return transacoes.stream().sorted(comparator).toList();
    }

    private void populateModelWithTransacaoData(Model model, List<projetojava.donodonegocio.model.Transacao> transacoes, 
                                               TransacaoFilterParams params, CustomUserDetails userDetails) {
        model.addAttribute("modo", "transacao");
        model.addAttribute("tipo", params.tipo().toLowerCase(Locale.ROOT));
        model.addAttribute("status", params.status().toLowerCase(Locale.ROOT));
        model.addAttribute("dataInicio", params.dataInicio());
        model.addAttribute("dataFim", params.dataFim());
        model.addAttribute("campoData", params.campoData());
        model.addAttribute("sort1", params.sort1());
        model.addAttribute("dir1", params.dir1());
        model.addAttribute("sort2", params.sort2());
        model.addAttribute("dir2", params.dir2());
        model.addAttribute("transacoes", transacoes);
        model.addAttribute("tiposTransacaoNomes", tipoTransacaoLookupService.listNomesParaTela(params.tipo()));

        addReferenceDataToModel(model, userDetails);
        addLabelMapsToModel(model, userDetails);
        addTransactionGroupsToModel(model, transacoes);
    }

    private void addReferenceDataToModel(Model model, CustomUserDetails userDetails) {
        model.addAttribute("clientes", clienteRepository.findByEmpresaIdOrderByIdLocalEmpresaAsc(userDetails.getEmpresaId()));
        model.addAttribute("produtos", produtoRepository.findByEmpresaIdOrderByIdLocalEmpresaAsc(userDetails.getEmpresaId()));
        model.addAttribute("tesouros", tesouroRepository.findByEmpresaIdOrderByIdLocalEmpresaAsc(userDetails.getEmpresaId()));
        
        List<Usuario> usuarios = usuarioRepository.findByEmpresaIdOrderByIdLocalEmpresaAsc(userDetails.getEmpresaId());
        List<Cliente> clientes = clienteRepository.findByEmpresaIdOrderByIdLocalEmpresaAsc(userDetails.getEmpresaId());
        List<Produto> produtos = produtoRepository.findByEmpresaIdOrderByIdLocalEmpresaAsc(userDetails.getEmpresaId());
        List<Tesouro> tesouros = tesouroRepository.findByEmpresaIdOrderByIdLocalEmpresaAsc(userDetails.getEmpresaId());

        model.addAttribute("usuarios", usuarios);
        model.addAttribute("clientes", clientes);
        model.addAttribute("produtos", produtos);
        model.addAttribute("tesouros", tesouros);
    }

    private void addLabelMapsToModel(Model model, CustomUserDetails userDetails) {
        List<Usuario> usuarios = usuarioRepository.findByEmpresaIdOrderByIdLocalEmpresaAsc(userDetails.getEmpresaId());
        List<Cliente> clientes = clienteRepository.findByEmpresaIdOrderByIdLocalEmpresaAsc(userDetails.getEmpresaId());
        List<Produto> produtos = produtoRepository.findByEmpresaIdOrderByIdLocalEmpresaAsc(userDetails.getEmpresaId());
        List<Tesouro> tesouros = tesouroRepository.findByEmpresaIdOrderByIdLocalEmpresaAsc(userDetails.getEmpresaId());

        model.addAttribute("labelUsuarios", usuarios.stream()
                .filter(u -> u.getIdLocalEmpresa() != null)
                .collect(Collectors.toMap(Usuario::getIdLocalEmpresa, Usuario::getLogin, (a, b) -> a, LinkedHashMap::new)));
        model.addAttribute("labelClientes", clientes.stream()
                .filter(c -> c.getIdLocalEmpresa() != null)
                .collect(Collectors.toMap(Cliente::getIdLocalEmpresa, Cliente::getNome, (a, b) -> a, LinkedHashMap::new)));
        model.addAttribute("labelProdutos", produtos.stream()
                .filter(p -> p.getIdLocalEmpresa() != null)
                .collect(Collectors.toMap(Produto::getIdLocalEmpresa, Produto::getNome, (a, b) -> a, LinkedHashMap::new)));
        model.addAttribute("labelTesouros", tesouros.stream()
                .filter(t -> t.getIdLocalEmpresa() != null)
                .collect(Collectors.toMap(Tesouro::getIdLocalEmpresa, Tesouro::getNomeConta, (a, b) -> a, LinkedHashMap::new)));
    }

    private void addTransactionGroupsToModel(Model model, List<projetojava.donodonegocio.model.Transacao> transacoes) {
        Map<String, List<projetojava.donodonegocio.model.Transacao>> grupos = transacoes.stream()
                .collect(Collectors.groupingBy(
                        projetojava.donodonegocio.model.Transacao::getGrupoId,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
        model.addAttribute("gruposTransacoes", grupos);
    }

    private LocalDate parseDate(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(raw);
        } catch (Exception ex) {
            return null;
        }
    }

    private CustomUserDetails getUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return null;
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof CustomUserDetails cud) {
            return cud;
        }
        return null;
    }

    private Comparator<projetojava.donodonegocio.model.Transacao> buildComparator(String sort1, String dir1, String sort2, String dir2) {
        Comparator<projetojava.donodonegocio.model.Transacao> c1 = comparatorFor(sort1, dir1);
        if (sort2 == null || sort2.isBlank()) {
            return c1;
        }
        return c1.thenComparing(comparatorFor(sort2, dir2));
    }

    private Comparator<projetojava.donodonegocio.model.Transacao> comparatorFor(String sort, String dir) {
        Comparator<projetojava.donodonegocio.model.Transacao> c;
        String key = sort == null ? "" : sort;
        switch (key) {
            case "tipo" -> c = Comparator.comparing(t -> t.getTipoTransacao() != null ? t.getTipoTransacao().getNome() : "", String.CASE_INSENSITIVE_ORDER);
            case "dataCriacao" -> c = Comparator.comparing(projetojava.donodonegocio.model.Transacao::getDataCriacao, Comparator.nullsLast(Comparator.naturalOrder()));
            case "dataResolucao" -> c = Comparator.comparing(projetojava.donodonegocio.model.Transacao::getDataResolucao, Comparator.nullsLast(Comparator.naturalOrder()));
            case "foiResolvido" -> c = Comparator.comparing(projetojava.donodonegocio.model.Transacao::isFoiResolvido);
            case "ehValida" -> c = Comparator.comparing(projetojava.donodonegocio.model.Transacao::isEhValida);
            case "responsavelId" -> c = Comparator.comparing(projetojava.donodonegocio.model.Transacao::getResponsavelId, Comparator.nullsLast(Comparator.naturalOrder()));
            case "tabelaResponsavel" -> c = Comparator.comparing(projetojava.donodonegocio.model.Transacao::getTabelaResponsavel, String.CASE_INSENSITIVE_ORDER);
            case "intervaloCobranca" -> c = Comparator.comparing(projetojava.donodonegocio.model.Transacao::getIntervaloCobranca, Comparator.nullsLast(Comparator.naturalOrder()));
            default -> c = Comparator.comparing(projetojava.donodonegocio.model.Transacao::getIdLocalEmpresa, Comparator.nullsLast(Comparator.naturalOrder()));
        }

        if ("desc".equalsIgnoreCase(dir)) {
            return c.reversed();
        }
        return c;
    }

    @GetMapping("/vendas")
    public String vendas() {
        return "vendas";
    }

    @GetMapping("/compras")
    public String compras() {
        return "compras";
    }

    @GetMapping("/movimentacoes")
    public String movimentacoes() {
        return "movimentacoes";
    }

    @GetMapping("/auditoria")
    public String auditoria() {
        return "auditoria";
    }

    @GetMapping("/cadastro")
    public String cadastro() {
        return "cadastro";
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