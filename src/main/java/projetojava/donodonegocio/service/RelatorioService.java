package projetojava.donodonegocio.service;

import projetojava.donodonegocio.dto.TransacaoDTO;
import projetojava.donodonegocio.mapper.TransacaoMapper;
import projetojava.donodonegocio.repository.MovimentacaoRepository;
import projetojava.donodonegocio.repository.TransacaoRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final TransacaoRepository transacaoRepository;
    private final MovimentacaoRepository movimentacaoRepository;
    private final TransacaoMapper transacaoMapper;

    @Getter
    @RequiredArgsConstructor
    public enum TipoRelatorio {
        DESPESA("Despesa"),
        RECEITA("Receita"),
        DEVENDO("Devendo"),
        QUITADO("Quitado"),
        ENTRADA_ESTOQUE("Entrada Estoque"),
        SAIDA_ESTOQUE("Saida Estoque"),
        VENDA("Venda"),
        COMPRA("Compra"),
        TRANSFERENCIA("Transferencia"),
        AJUSTE("Ajuste");

        private final String nome;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'CONSULTOR')")
    public List<TransacaoDTO> getRelatorioTransacoes(Long empresaId, TipoRelatorio tipo) {
        return transacaoRepository.findByEmpresaIdAndTipoTransacaoNome(empresaId, tipo.getNome()).stream()
                .map(transacaoMapper::toDTO)
                .collect(Collectors.toList());
    }

    private List<TransacaoDTO> applyFiltroTransacoes(List<TransacaoDTO> transacoes,
                                                String status,
                                                LocalDate dataInicio,
                                                LocalDate dataFim,
                                                String campoData) {
        if (transacoes == null) {
            return List.of();
        }

        List<TransacaoDTO> out = filterByStatus(transacoes, status);
        
        if (dataInicio == null && dataFim == null) {
            return out;
        }
        
        return filterByDateRange(out, dataInicio, dataFim, campoData);
    }

    private List<TransacaoDTO> filterByStatus(List<TransacaoDTO> transacoes, String status) {
        String st = status == null ? "todos" : status;
        if ("ativos".equalsIgnoreCase(st)) {
            return transacoes.stream().filter(TransacaoDTO::isEhValida).toList();
        } else if ("inativos".equalsIgnoreCase(st)) {
            return transacoes.stream().filter(t -> !t.isEhValida()).toList();
        } else if ("quitados".equalsIgnoreCase(st)) {
            return transacoes.stream().filter(TransacaoDTO::isFoiResolvido).toList();
        } else if ("devendo".equalsIgnoreCase(st)) {
            return filterDevendoTransacoes(transacoes);
        }
        return transacoes;
    }

    private List<TransacaoDTO> filterDevendoTransacoes(List<TransacaoDTO> transacoes) {
        LocalDateTime agora = LocalDateTime.now();
        return transacoes.stream()
                .filter(TransacaoDTO::isEhValida)
                .filter(t -> !t.isFoiResolvido())
                .filter(t -> t.getIntervaloCobranca() != null && t.getIntervaloCobranca() > 0)
                .filter(t -> t.getDataCriacao() != null)
                .filter(t -> t.getDataCriacao().plusDays(t.getIntervaloCobranca()).isBefore(agora)
                        || t.getDataCriacao().plusDays(t.getIntervaloCobranca()).isEqual(agora))
                .toList();
    }

    private List<TransacaoDTO> filterByDateRange(List<TransacaoDTO> transacoes, LocalDate dataInicio, LocalDate dataFim, String campoData) {
        String campo = campoData == null ? "auto" : campoData;

        return transacoes.stream().filter(t -> {
            LocalDate d = getDateForFiltering(t, campo);
            if (d == null) return false;
            if (dataInicio != null && d.isBefore(dataInicio)) return false;
            return dataFim == null || !d.isAfter(dataFim);
        }).toList();
    }

    private LocalDate getDateForFiltering(TransacaoDTO t, String campo) {
        if ("criacao".equalsIgnoreCase(campo)) {
            return toLocalDate(t.getDataCriacao());
        } 
        if ("resolucao".equalsIgnoreCase(campo)) {
            return toLocalDate(t.getDataResolucao());
        }
        return getAutoDate(t);
    }

    private LocalDate toLocalDate(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.toLocalDate() : null;
    }

    private LocalDate getAutoDate(TransacaoDTO t) {
        if (t.isFoiResolvido()) {
            return toLocalDate(t.getDataResolucao());
        }
        return toLocalDate(t.getDataCriacao());
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'CONSULTOR')")
    public List<TransacaoDTO> filtrarTransacoesParaRelatorio(Long empresaId, TipoRelatorio tipo, String status, LocalDate dataInicio, LocalDate dataFim, String campoData) {
        List<TransacaoDTO> transacoes = transacaoRepository.findByEmpresaIdAndTipoTransacaoNome(empresaId, tipo.getNome()).stream()
                .map(transacaoMapper::toDTO)
                .collect(Collectors.toList());
        
        return applyFiltroTransacoes(transacoes, status, dataInicio, dataFim, campoData);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')") // Apenas Admin vê Lucro
    public BigDecimal getLucro(Long empresaId) {
        BigDecimal saldo = movimentacaoRepository.calcularSaldoTesouro(empresaId);
        return saldo != null ? saldo : BigDecimal.ZERO;
    }

    public List<TransacaoDTO> filtrarTransacoes(List<TransacaoDTO> transacoes, String status, LocalDate dataInicio, LocalDate dataFim, String campoData) {
        return applyFiltroTransacoes(transacoes, status, dataInicio, dataFim, campoData);
    }
}