package projetojava.donodonegocio.controller;

import lombok.Getter;
import lombok.Setter;
import projetojava.donodonegocio.dto.TransacaoDTO;
import projetojava.donodonegocio.mapper.TransacaoMapper;
import projetojava.donodonegocio.model.Transacao;
import projetojava.donodonegocio.model.TipoTransacao;
import projetojava.donodonegocio.repository.TransacaoRepository;
import projetojava.donodonegocio.security.CustomUserDetails;
import projetojava.donodonegocio.service.TipoTransacaoLookupService;
import projetojava.donodonegocio.service.TransacaoService;
import projetojava.donodonegocio.config.AppConstants;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.validation.Valid;

@Controller
@RequiredArgsConstructor
public class TransacaoTableController {

    private final TransacaoService transacaoService;
    private final TransacaoRepository transacaoRepository;
    private final TransacaoMapper transacaoMapper;
    private final TipoTransacaoLookupService tipoTransacaoLookupService;

    @PostMapping("/transacoes/salvar")
    public String salvar(@Valid TransacaoForm form, BindingResult bindingResult, Model model) {
        CustomUserDetails user = getUserDetails();
        if (user == null) {
            return AppConstants.REDIRECT_LOGIN;
        }

        String tipo = form.getTipoTela();
        if (!"venda".equalsIgnoreCase(tipo) && !"compra".equalsIgnoreCase(tipo)) {
            model.addAttribute(AppConstants.ERROR_MESSAGE, "Tipo de transação inválido");
            return AppConstants.REDIRECT_TABELAS;
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute(AppConstants.ERROR_MESSAGE, "Dados inválidos. Verifique os campos obrigatórios.");
            return AppConstants.REDIRECT_TABELAS_WITH_TIPO + tipo;
        }

        TransacaoDTO dto = new TransacaoDTO();
        dto.setEmpresaId(user.getEmpresaId());
        dto.setIdLocalEmpresa(form.getIdLocalEmpresa());
        dto.setUsuarioId(user.getIdLocalEmpresa() != null ? user.getIdLocalEmpresa().longValue() : null);

        TipoTransacao tipoTransacao = tipoTransacaoLookupService.findByNome(user.getEmpresaId(), form.getTipoNome()).orElse(null);
        if (tipoTransacao == null || tipoTransacao.getIdLocalEmpresa() == null) {
            model.addAttribute(AppConstants.ERROR_MESSAGE, "Tipo de transação indisponível para esta empresa.");
            return AppConstants.REDIRECT_TABELAS_WITH_TIPO + tipo;
        }

        dto.setTipoId(tipoTransacao.getIdLocalEmpresa().longValue());
        dto.setTabelaResponsavel(form.getTabelaResponsavel());
        dto.setResponsavelId(form.getResponsavelId());
        dto.setIntervaloCobranca(form.getIntervaloCobranca());
        dto.setFoiResolvido(Boolean.TRUE.equals(form.getFoiResolvido()));
        dto.setEhValida(true);

        transacaoService.salvar(dto);

        return AppConstants.REDIRECT_TABELAS_WITH_TIPO + tipo + AppConstants.STATUS_TODOS;
    }

    @PostMapping("/transacoes/quitar")
    public String quitar(@RequestParam("idLocalEmpresa") @NotNull Integer idLocalEmpresa,
                         @RequestParam("tipo") String tipo) {
        CustomUserDetails user = getUserDetails();
        if (user == null) {
            return AppConstants.REDIRECT_LOGIN;
        }

        transacaoService.resolver(user.getEmpresaId(), idLocalEmpresa);
        return AppConstants.REDIRECT_TABELAS_WITH_TIPO + tipo + AppConstants.STATUS_TODOS;
    }

    @PostMapping("/transacoes/grupo/criar")
    public String criarGrupo(@Valid @ModelAttribute GrupoTransacaoForm form, BindingResult bindingResult, Model model) {
        CustomUserDetails user = getUserDetails();
        if (user == null) {
            return AppConstants.REDIRECT_LOGIN;
        }
        String tipo = form.getTipoTela();
        if (!"venda".equalsIgnoreCase(tipo) && !"compra".equalsIgnoreCase(tipo)) {
            model.addAttribute(AppConstants.ERROR_MESSAGE, "Tipo de transação inválido");
            return AppConstants.REDIRECT_TABELAS;
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute(AppConstants.ERROR_MESSAGE, "Dados inválidos. Verifique os campos obrigatórios.");
            return AppConstants.REDIRECT_TABELAS_WITH_TIPO + tipo;
        }

        String tipoNome = form.getTipoNome();

        transacaoService.criarGrupo(
                tipoNome,
                user.getEmpresaId(),
                user.getIdLocalEmpresa(),
                form.getClienteId(),
                form.getProdutoId(),
                form.getTesouroId(),
                form.getIntervaloCobranca()
        );

        return AppConstants.REDIRECT_TABELAS_WITH_TIPO + tipo + AppConstants.STATUS_TODOS;
    }

    @PostMapping("/transacoes/grupo/toggle-status")
    public String toggleStatusGrupo(@RequestParam("grupoId") String grupoId,
                                    @RequestParam("tipo") String tipo) {
        CustomUserDetails user = getUserDetails();
        if (user == null) {
            return AppConstants.REDIRECT_LOGIN;
        }

        transacaoService.toggleValidadeGrupo(user.getEmpresaId(), grupoId);
        return AppConstants.REDIRECT_TABELAS_WITH_TIPO + tipo + AppConstants.STATUS_TODOS;
    }

    @PostMapping("/transacoes/grupo/quitar")
    public String quitarGrupo(@RequestParam("grupoId") String grupoId,
                              @RequestParam("tipo") String tipo,
                              Model model) {
        CustomUserDetails user = getUserDetails();
        if (user == null) {
            return AppConstants.REDIRECT_LOGIN;
        }

        transacaoService.quitarGrupo(user.getEmpresaId(), grupoId);
        return AppConstants.REDIRECT_TABELAS_WITH_TIPO + tipo + AppConstants.STATUS_TODOS;
    }

    @PostMapping("/transacoes/toggle-status")
    public String toggleStatus(@RequestParam("idLocalEmpresa") @NotNull Integer idLocalEmpresa,
                               @RequestParam("tipo") String tipo,
                               Model model) {
        CustomUserDetails user = getUserDetails();
        if (user == null) {
            return AppConstants.REDIRECT_LOGIN;
        }

        Transacao t = transacaoRepository.findByEmpresaIdAndIdLocalEmpresa(user.getEmpresaId(), idLocalEmpresa)
                .orElse(null);

        if (t != null) {
            t.setEhValida(!t.isEhValida());
            transacaoRepository.save(t);
        }

        return AppConstants.REDIRECT_TABELAS_WITH_TIPO + tipo + AppConstants.STATUS_TODOS;
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

    @Getter
    @Setter
    public static class TransacaoForm {
        private Long idLocalEmpresa;
        private String tipoNome;
        private String tabelaResponsavel;
        private Integer responsavelId;
        private Short intervaloCobranca;
        private Boolean foiResolvido;
        private String tipoTela;
    }

    @Getter
    @Setter
    public static class GrupoTransacaoForm {
        private String tipoTela;
        private String tipoNome;
        private Integer clienteId;
        private Integer produtoId;
        private Integer tesouroId;
        private Short intervaloCobranca;
    }
}
